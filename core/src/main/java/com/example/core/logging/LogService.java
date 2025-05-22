package com.example.core.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogService {
    private static final LogService instance = new LogService();

    private LogService() {
        Logger log = Logger.getLogger("");
        log.setLevel(Level.ALL);
    }

    public static LogService get() {
        return instance;
    }

    public static void info(String s) {
        Logger.getLogger("app").info(s);
    }

    public static void warning(String s) {
        Logger.getLogger("app").warning(s);
    }

    public Logger forClass(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }

    public void setGlobalLevel(Level level) {
        Logger.getLogger("").setLevel(level);
    }

    public void setAppLevel(Level level) {
        Logger.getLogger("app").setLevel(level);
    }
}
