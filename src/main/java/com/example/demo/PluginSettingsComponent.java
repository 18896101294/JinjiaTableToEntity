package com.example.demo;

import com.example.demo.model.KeyValuePair;
import com.example.demo.model.TreeModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginSettingsComponent {
    private JPanel panel;
    private JComboBox<KeyValuePair<String, String>> templateComboBox;
    private JComboBox<KeyValuePair<String, String>> databaseComboBox;

    public PluginSettingsComponent() {
        panel = new JPanel(new GridBagLayout());  // 使用 GridBagLayout 来精确控制布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST; // 设置左对齐
        gbc.insets = JBUI.insets(5); // 设置一些间距

        // 第一行: 模板
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 0;  // 第一行
        panel.add(new JLabel("生成模板："), gbc);

        gbc.gridx = 1;  // 第二列
        templateComboBox = new ComboBox<>();
        panel.add(templateComboBox, gbc);

        // 第二行: 数据库名 (下拉框)
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 1;  // 第二行
        panel.add(new JLabel("数据库连接："), gbc);

        gbc.gridx = 1;  // 第二列
        databaseComboBox = new ComboBox<>();
        panel.add(databaseComboBox, gbc);

        // 加载并填充数据库连接下拉
        loadAndFillDatabaseComboBox(null);
        loadAndFillTemplateComboBox(null);
    }

    public JPanel getPanel() {
        return panel;
    }

    // 获取选择的模板
    @SuppressWarnings("unchecked")
    public KeyValuePair<String, String> getSelectedTemplate() {
        if (templateComboBox.getSelectedItem() != null) {
            return (KeyValuePair<String, String>) templateComboBox.getSelectedItem();
        }

        return null;
    }

    public void setSelectedTemplate(KeyValuePair<String, String> keyValuePair) {
        if (keyValuePair.getKey().equals("0")) {
            return;
        }
        loadAndFillTemplateComboBox(keyValuePair);
    }

    // 获取选择的数据库名
    @SuppressWarnings("unchecked")
    public KeyValuePair<String, String> getSelectedDatabase() {
        if (databaseComboBox.getSelectedItem() != null) {
            return (KeyValuePair<String, String>) databaseComboBox.getSelectedItem();
        }

        return null;
    }

    // 设置默认选择
    public void setSelectedDatabase(KeyValuePair<String, String> keyValuePair) {
        if (keyValuePair.getKey().equals("0"))
            return;
        loadAndFillDatabaseComboBox(keyValuePair);
    }

    // 解析API返回的字符串并填充到下拉框
    private void loadAndFillDatabaseComboBox(KeyValuePair<String, String> keyValuePair) {
        var databaseList = new CommonHelper().getDatabase(); // 解析 JSON 返回的结果
        var pairArrayList = parseResponse(databaseList);

        SwingUtilities.invokeLater(() -> {
            databaseComboBox.removeAllItems(); // 清空旧的数据
            databaseComboBox.addItem(new KeyValuePair<>("0", "请选择（默认为sit库）")); // 添加新的数据
//            databaseComboBox.setSelectedIndex(1);
            for (KeyValuePair<String, String> model : pairArrayList) {
                databaseComboBox.addItem(model); // 添加新的数据
                if (keyValuePair != null && Objects.equals(model.getKey(), keyValuePair.getKey())) {
                    databaseComboBox.setSelectedItem(model);
                }
            }
        });
    }

    private void loadAndFillTemplateComboBox(KeyValuePair<String, String> keyValuePair) {
        var templateList = new CommonHelper().getTemplate(1); // 解析 JSON 返回的结果
        var pairArrayList = parseResponse(templateList);

        SwingUtilities.invokeLater(() -> {
            templateComboBox.removeAllItems(); // 清空旧的数据
            templateComboBox.addItem(new KeyValuePair<>("0", "请选择（默认为简单实体）")); // 添加新的数据
            templateComboBox.setSelectedIndex(0);
            for (KeyValuePair<String, String> model : pairArrayList) {
                templateComboBox.addItem(model); // 添加新的数据
                if (keyValuePair != null && Objects.equals(model.getKey(), keyValuePair.getKey())) {
                    templateComboBox.setSelectedItem(model);
                }
            }
        });
    }

    // 构建键值对形式
    private java.util.@NotNull List<KeyValuePair<String, String>> parseResponse(java.util.@NotNull List<TreeModel> treeModels) {

        List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<>();

        for (TreeModel treeModel : treeModels) {
            keyValuePairs.add(new KeyValuePair<>(treeModel.id, treeModel.title));
        }

        return keyValuePairs;
    }
}