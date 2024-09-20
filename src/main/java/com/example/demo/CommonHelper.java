package com.example.demo;

import com.example.demo.model.TreeModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommonHelper {
    // 获取类文件内容
    public String sendPostRequest(String jsonBody) throws Exception {
        // 创建 HttpClient 实例
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // 构建 POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://10.10.22.39:5050/api/codetable/CopyV2"))
                    .timeout(Duration.ofSeconds(30)) // 设置请求超时
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            // 发送请求并获取响应
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // 检查响应码
        if (response.statusCode() == 200) {
            String body = response.body();
            JSONObject jsonResponse = new JSONObject(body);
            if (Boolean.parseBoolean(jsonResponse.get("IsSuccess").toString())) {
                return jsonResponse.get("Data").toString();
            } else {
                Messages.showMessageDialog("同步失败：" + jsonResponse.get("Data").toString(), "错误", Messages.getErrorIcon());
                return "";
            }
        } else {
            throw new RuntimeException("请求失败，响应码: " + response.statusCode());
        }
    }

    // 获取数据库下拉源
    public List<TreeModel> getDatabase() {
        // 创建 HttpClient 实例
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // 构建 POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://10.10.22.39:5050/api/system/getdatabase"))
                    .timeout(Duration.ofSeconds(30)) // 设置请求超时
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("", StandardCharsets.UTF_8))
                    .build();

            // 发送请求并获取响应
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 检查响应码
        return getTreeModels(response);
    }

    // 获取模板下拉源
    public List<TreeModel> getTemplate(Integer type) {
        // 创建 HttpClient 实例
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // 构建 POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://10.10.22.39:5050/api/system/getTemplate?type=" + type))
                    .timeout(Duration.ofSeconds(30)) // 设置请求超时
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("", StandardCharsets.UTF_8))
                    .build();

            // 发送请求并获取响应
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 检查响应码
        return getTreeModels(response);
    }

    private List<TreeModel> getTreeModels(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            String body = response.body();
            JSONObject jsonResponse = new JSONObject(body);
            if (Boolean.parseBoolean(jsonResponse.get("IsSuccess").toString())) {
                String jsonString = jsonResponse.get("Data").toString();

                return JSON.parseArray(jsonString, TreeModel.class);
            } else {
                Messages.showMessageDialog("同步失败：" + jsonResponse.get("Data").toString(), "错误", Messages.getErrorIcon());
                return new ArrayList<>();
            }
        } else {
            throw new RuntimeException("请求失败，响应码: " + response.statusCode());
        }
    }

    // 转换为下划线格式的方法
    public String convertToSnakeCase(@NotNull String input) {
        // 如果输入文本以 @ 开头，直接返回原文本
        if (input.startsWith("@")) {
            return input;
        }

        // 将大写字母前面插入下划线，并将结果转为小写
        return input
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")  // 找到小写字母后面紧跟的大写字母
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")  // 找到连续的大写字母，后面跟着小写字母
                .toLowerCase();  // 转换为全小写
    }

    // 获取文件的命名空间
    public String GetNameSpace(VirtualFile newFile) {
        // 提取最后一个/后面的文本
        StringBuilder filePath = new StringBuilder();
        var parentFile = newFile;
        int count = 0;
        while (true) {
            if (count == 10)
                break;
            var parentPath = parentFile.getParent().getPath();
            if (parentPath.equals("/"))
                break;
            String itemPath = parentPath.substring(parentPath.lastIndexOf("/") + 1);
            if (itemPath.contains("Jinjia") && itemPath.contains("Model")) {
                break;
            }

            if (parentFile.equals(newFile)) {
                filePath.insert(0, itemPath);
            } else {
                filePath.insert(0, itemPath + ".");
            }
            parentFile = parentFile.getParent();

            count = count + 1;
        }

        return filePath.toString();
    }

    // 返回用户的配置
    public List<String> GetSelectedKey() {
        List<String> result = new ArrayList<>();
        PluginSettingsState settingsState = PluginSettingsState.getInstance();
        // 获取下拉框中的数据库和值模板
        String selectedDatabase = settingsState.databaseKeyValue.getKey();
        selectedDatabase = Objects.equals(selectedDatabase, "0") ? "2" : selectedDatabase;
        String selectedTemplate = settingsState.templateKeyValue.getKey();
        selectedTemplate = Objects.equals(selectedTemplate, "0") ? "1203" : selectedTemplate;
        result.add(selectedDatabase);
        result.add(selectedTemplate);

        return result;
    }
}
