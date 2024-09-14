package com.example.demo;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

// 数据库表新建到类
public class MyFolderAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 获取当前的项目
        Project project = event.getData(CommonDataKeys.PROJECT);
        // 获取用户右键点击的文件夹或项目
        VirtualFile folder = event.getData(CommonDataKeys.VIRTUAL_FILE);

        // 判断是否为文件夹或项目根目录
        if (project != null) {
            // 弹出输入框让用户输入文件名
            String userInput = Messages.showInputDialog(
                    project,
                    "请输入新建文件的名称：",
                    "新建文件",
                    Messages.getQuestionIcon()
            );

            // 检查用户是否输入了内容
            if (userInput != null && !userInput.trim().isEmpty()) {

                // 在指定的文件夹中创建文件
                createNewFile(project, folder, userInput);
            } else {
                Messages.showMessageDialog(project, "文件名不能为空", "错误", Messages.getErrorIcon());
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 获取文件和项目
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getData(CommonDataKeys.PROJECT);

        // 使操作在用户右键点击项目根目录或文件夹时可见
        boolean isVisible = project != null || (file == null || file.isDirectory());
        e.getPresentation().setEnabledAndVisible(isVisible);
    }

    // 重写 getActionUpdateThread 方法，选择在 EDT 上执行
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // 由于我们处理的是 UI 操作，因此选择 EDT 线程
        return ActionUpdateThread.EDT;
    }

    // 创建新文件的方法
    private void createNewFile(Project project, VirtualFile folder, String userInput) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                // 确保文件名以 .cs 结尾
                String fileName = userInput.endsWith(".cs") ? userInput : userInput + ".cs";
                // 检查文件是否已经存在
                VirtualFile existingFile = folder.findChild(fileName);
                if (existingFile != null) {
                    Messages.showMessageDialog(project, "文件已存在: " + fileName, "错误", Messages.getErrorIcon());
                } else {
                    // 创建新文件
                    VirtualFile newFile = folder.createChildData(this, fileName);

                    JSONObject jsonBody = getJsonObject(project, userInput, newFile);

                    // 调用 API
                    try {
                        CommonHelper helper = new CommonHelper();
                        String classText = helper.sendPostRequest(jsonBody.toString());
                        if (Objects.equals(classText, "")) {
                            return;
                        }

                        // 将 Data 字段内容替换到当前文件中
                        // 设置文件内容（可以是空的或者预定义的内容）
                        VfsUtil.saveText(newFile, classText);
                        // 打开新创建的文件
                        FileEditorManager.getInstance(project).openFile(newFile, true);
                        // 在项目视图中选中新创建的文件
                        ProjectView.getInstance(project).select(null, newFile, true);

                    } catch (Exception e) {
                        Messages.showMessageDialog("请求失败: " + e.getMessage(), "错误", Messages.getErrorIcon());
                    }
                }
            } catch (IOException e) {
                Messages.showMessageDialog(project, "创建文件时出错: " + e.getMessage(), "错误", Messages.getErrorIcon());
            }
        });
    }

    private static @NotNull JSONObject getJsonObject(Project project, String userInput, VirtualFile newFile) {
        // 提取最后一个/后面的文本
        StringBuilder filePath = new StringBuilder();
        var parentFile = newFile;
        while (true) {
            var parentPath = parentFile.getParent().getPath();
            String itemPath = parentPath.substring(parentPath.lastIndexOf("/") + 1);
            if (!itemPath.equals(project.getName())) {
                if (parentFile.equals(newFile)) {
                    filePath.insert(0, itemPath);
                } else {
                    filePath.insert(0, itemPath + ".");
                }
                parentFile = parentFile.getParent();
            } else {
                break;
            }
        }

        // 将选中的文本转换为下划线格式
        if (userInput != null) {
            CommonHelper helper = new CommonHelper();
            userInput = helper.convertToSnakeCase(userInput);
        }

        String projectName = filePath.toString();
        // 构建 JSON 数据
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Tables", userInput);
        jsonBody.put("ProjectName", projectName);
        jsonBody.put("ProjectId", 1203);
        jsonBody.put("DbId", 2);
        return jsonBody;
    }
}