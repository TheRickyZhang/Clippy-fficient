package com.example.core.search;

import org.jetbrains.annotations.NotNull;

public record SearchEntry (String pageName, String text, Runnable action){
    @Override @NotNull
    public String toString() {
        return pageName + " > " + text;
    }
}
