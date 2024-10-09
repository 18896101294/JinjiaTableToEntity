package com.example.demo;

import com.example.demo.model.KeyValuePair;
import com.example.demo.model.TreeModel;
import com.intellij.ui.JBColor;
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
        // 创建主面板并设置背景颜色
        panel = new JPanel(new GridBagLayout());
        panel.setBackground(JBUI.CurrentTheme.CustomFrameDecorations.paneBackground());  // 设定面板背景颜色

        // 设置带有内边距的边框，让面板有更多的视觉空间
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("配置设置"),  // 带有标题的边框
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));  // 内边距

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;  // 左对齐
        gbc.insets = JBUI.insets(10);  // 设置内边距，使组件间更均匀

        // 设置字体样式
        Font labelFont = new Font("Microsoft YaHei", Font.BOLD, 12);

        // 第一行: 模板标签和下拉框
        addComponent(panel, gbc, "生成模板：", labelFont, templateComboBox = new JComboBox<>(), 0);

        // 第二行: 数据库连接标签和下拉框
        addComponent(panel, gbc, "数据库连接：", labelFont, databaseComboBox = new JComboBox<>(), 1);

        // 设置组件的背景颜色（可选），提升视觉层次感
        templateComboBox.setBackground(JBColor.WHITE);
        databaseComboBox.setBackground(JBColor.WHITE);

        // 加载并填充数据库连接下拉
        loadAndFillDatabaseComboBox(null);
        loadAndFillTemplateComboBox(null);
    }

    // 添加标签和下拉框的方法
    private void addComponent(JPanel panel, GridBagConstraints gbc, String labelText, Font font, JComponent component, int gridY) {
        gbc.gridx = 0;  // 第一列
        gbc.gridy = gridY;
        JLabel label = new JLabel(labelText);
        label.setFont(font);  // 应用字体
        panel.add(label, gbc);

        gbc.gridx = 1;  // 第二列
        panel.add(component, gbc);
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
            templateComboBox.addItem(new KeyValuePair<>("0", "请选择（默认为EasyEntity）")); // 添加新的数据
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