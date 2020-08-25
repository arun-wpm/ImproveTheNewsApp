package com.improvethenews.projecta;

public class Topic {
    String title;
    String mnemonic;
    int depth;

    Topic(String title, String mnemonic, int depth) {
        this.title = title;
        this.mnemonic = mnemonic;
        this.depth = depth;
    }

    public String getTitle() {
        return title;
    }
    public String getMnemonic() {
        return mnemonic;
    }
    public int getDepth() {
        return depth;
    }
}
