package com.example.config;

import java.util.Properties;

public class Env {
    private static final Properties P = new Properties();
    static {
        try (var in = Env.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) throw new RuntimeException("…not found");
            P.load(in);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public static String get(String k) { return P.getProperty(k); }
}
