package com.example.core;

import com.example.core.sequence.SequencePattern;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class SuggestionRegistry {
    private static final Map<SequencePattern,String> suggestions = new ConcurrentHashMap<>();

    public static void initDefaults() { /* load pattern→hint strings */ }
    public static void addCustom(SequencePattern p, String hint) { suggestions.put(p, hint); }
    public static void remove(SequencePattern p)           { suggestions.remove(p); }
    public static Map<SequencePattern,String> all()        { return Map.copyOf(suggestions); }
    private SuggestionRegistry() { }
}
