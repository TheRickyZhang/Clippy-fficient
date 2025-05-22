package com.example.core.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogService {
    private static final LogService instance = new LogService();
    private static final String APP_DETAILED = "com.example.detailed";
    private static final String APP_SIMPLE = "com.example.simple";

    private LogService() {
        Logger log = Logger.getLogger("");
        log.setLevel(Level.ALL);
    }

    public static LogService get() {
        return instance;
    }
    public static Logger detailed() {
        return Logger.getLogger(APP_DETAILED);
    }
    public static Logger simple() {
        return Logger.getLogger(APP_SIMPLE);
    }

    public Logger forClass(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }

//    public void setGlobalLevel(Level level) {
//        Logger.getLogger("").setLevel(level);
//    }

    // TO LOOK: Does this actually set the app level through com.example hierarchy?
//    public void setAppLevel(Level level) {
//        Logger.getLogger("com.example").setLevel(level);
//    }
}
