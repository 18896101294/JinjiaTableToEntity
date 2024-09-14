package com.example.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

import java.util.Objects;

// 自定义编辑器右键 => 同步数据库表字段到当前类
public class MyFirstAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        CommonHelper helper = new CommonHelper();
        // 获取当前的项目
        Project project = event.getData(CommonDataKeys.PROJECT);
        String projectName = null;

        if (project != null) {
            // 获取当前打开的文件
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                // 给用户显示确认对话框
                int result = Messages.showYesNoDialog(
                        "请注意会覆盖当前类？",
                        "确认操作",
                        Messages.getQuestionIcon()
                );

                if (result != Messages.YES) {
                    return;
                }

                VirtualFile currentFile = FileEditorManager.getInstance(project).getSelectedFiles()[0];

                String filePath = currentFile.getParent().getPath();
                // 提取最后一个/后面的文本
                projectName = filePath.substring(filePath.lastIndexOf("/") + 1);
            } else {
                Messages.showMessageDialog("没有打开任何文件。", "错误", Messages.getErrorIcon());
            }
        } else {
            Messages.showMessageDialog("无法获取当前的项目。", "错误", Messages.getErrorIcon());
        }

        // 获取当前的编辑器
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (editor != null) {
            // 获取 CaretModel 和 SelectionModel
            // CaretModel caretModel = editor.getCaretModel();
            SelectionModel selectionModel = editor.getSelectionModel();

            // 检查是否有选中的文本
            if (selectionModel.hasSelection()) {
                // 获取选中的文本
                String selectedText = selectionModel.getSelectedText();

                // 将选中的文本转换为下划线格式
                if (selectedText != null) {
                    selectedText = helper.convertToSnakeCase(selectedText);
                }

                // 显示选中的文本
                // Messages.showMessageDialog("你选中的文本是: " + selectedText, "选中文本", Messages.getInformationIcon());

                // 构建 JSON 数据
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("Tables", selectedText);
                jsonBody.put("ProjectName", projectName);
                jsonBody.put("ProjectId", 1203);
                jsonBody.put("DbId", 2);

                // 调用 API
                try {
                    String classText = helper.sendPostRequest(jsonBody.toString());
                    if (Objects.equals(classText, "")) {
                        return;
                    }

                    // 将 Data 字段内容替换到当前文件中
                    replaceFileContent(project, editor, classText);

                } catch (Exception e) {
                    Messages.showMessageDialog("请求失败: " + e.getMessage(), "错误", Messages.getErrorIcon());
                }

            } else {
                Messages.showMessageDialog("没有选中任何文本。", "选中文本", Messages.getWarningIcon());
            }
        } else {
            Messages.showMessageDialog("无法获取编辑器。", "错误", Messages.getErrorIcon());
        }
    }

    // 替换文件内容
    private void replaceFileContent(Project project, Editor editor, String newContent) {
        // 获取当前文件的 Document
        Document document = editor.getDocument();
        // 使用 WriteCommandAction.runWriteCommandAction 来确保写操作在正确的写入上下文中
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 使用 StringUtil 来统一行分隔符为 Unix 风格 '\n'，如果使用\r\n风格非windows系统会报错
            String unifiedContent = StringUtil.convertLineSeparators(newContent);
            document.setText(unifiedContent);
        });
    }
}