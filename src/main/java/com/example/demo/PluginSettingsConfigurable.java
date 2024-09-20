package com.example.demo;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsConfigurable implements Configurable {
    private PluginSettingsComponent settingsComponent;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "JinjiaTableToEntity Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsComponent = new PluginSettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        PluginSettingsState settingsState = PluginSettingsState.getInstance();
        var templateKeyValue = settingsComponent.getSelectedTemplate();
        var databaseKeyValue = settingsComponent.getSelectedDatabase();

        return (templateKeyValue != null && !settingsComponent.getSelectedTemplate().getKey().equals(settingsState.templateKeyValue.getKey())) ||
                (databaseKeyValue != null && !settingsComponent.getSelectedDatabase().getKey().equals(settingsState.databaseKeyValue.getKey()));
    }

    @Override
    public void apply() {
        PluginSettingsState settingsState = PluginSettingsState.getInstance();
        settingsState.templateKeyValue = settingsComponent.getSelectedTemplate();
        settingsState.databaseKeyValue = settingsComponent.getSelectedDatabase();
    }

    @Override
    public void reset() {
        PluginSettingsState settingsState = PluginSettingsState.getInstance();
        settingsComponent.setSelectedDatabase(settingsState.databaseKeyValue);
        settingsComponent.setSelectedTemplate(settingsState.templateKeyValue);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}