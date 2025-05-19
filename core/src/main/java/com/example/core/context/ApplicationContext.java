package com.example.core.context;

import java.util.ArrayList;
import java.util.List;

public record ApplicationContext(List<String> info) {
    public ApplicationContext combine(ApplicationContext other) {
        List<String> combinedInfo = new ArrayList<>(this.info);
        combinedInfo.addAll(other.info);
        return new ApplicationContext(combinedInfo);
    }
}