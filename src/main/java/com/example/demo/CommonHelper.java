package com.example.demo;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class CommonHelper {
    // 获取类文件内容
    public String sendPostRequest(String jsonBody) throws Exception {
        // 创建 HttpClient 实例
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // 构建 POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://10.10.22.39:5050/api/codetable/CopyV2"))
                    .timeout(Duration.ofSeconds(10)) // 设置请求超时
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

    // 转换为下划线格式的方法
    public String convertToSnakeCase(String input) {
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
        while (true) {
            var parentPath = parentFile.getParent().getPath();
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
        }

        return filePath.toString();
    }
}
