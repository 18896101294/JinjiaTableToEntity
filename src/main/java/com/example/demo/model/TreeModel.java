package com.example.demo.model;

import java.util.List;

// 树形节点
public class TreeModel {
    public String id;

    public void SetId(String id) {
        this.id = id;
    }

    public String GetId() {
        return this.id;
    }

    public String title;

    public void SetTitle(String title) {
        this.title = title;
    }

    public String GetTitle() {
        return this.title;
    }

    public Boolean isSelectable;

    public void SetIsSelectable(Boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public Boolean GetIsSelectable() {
        return this.isSelectable;
    }

    public List<TreeModel> subs;

    public void SetSubs(List<TreeModel> subs) {
        this.subs = subs;
    }

    public List<TreeModel> GetSubs() {
        return this.subs;
    }
}
