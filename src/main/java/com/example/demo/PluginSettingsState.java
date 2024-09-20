package com.example.demo;

import com.example.demo.model.KeyValuePair;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.example.demo.PluginSettingsState",
        storages = {@Storage("JinjiaTableToEntityPluginSettings.xml")}
)
public class PluginSettingsState implements PersistentStateComponent<PluginSettingsState> {
    public KeyValuePair<String, String> databaseKeyValue = new KeyValuePair<>("0", "请选择（默认为sit库）"); // 默认sit
    public KeyValuePair<String, String> templateKeyValue = new KeyValuePair<>("0", "请选择（默认为简单实体）"); // 默认 简单实体

    public static PluginSettingsState getInstance() {
        return ServiceManager.getService(PluginSettingsState.class);
    }

    @Nullable
    @Override
    public PluginSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginSettingsState state) {
        this.databaseKeyValue = state.databaseKeyValue;
        this.templateKeyValue = state.templateKeyValue;
    }
}