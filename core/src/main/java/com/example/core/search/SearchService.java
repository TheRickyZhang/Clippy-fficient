package com.example.core.search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SearchService {
    private static final SearchService INSTANCE = new SearchService();
    private final Map<String, Supplier<List<SearchEntry>>> providers = new LinkedHashMap<>();

    public SearchService() {

    }

    public static SearchService get() {
        return INSTANCE;
    }

    public void registerProvider(String pageName, Supplier<List<SearchEntry>> sup) {
        providers.put(pageName, sup);
    }

    public List<SearchEntry> search(String q) {
        String lc = q.toLowerCase();
        return providers.values().stream()
                .flatMap(s -> s.get().stream())
                .filter(e -> e.text().toLowerCase().contains(lc))
                .toList();
    }

}
