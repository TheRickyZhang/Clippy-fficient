// src/main/java/com/example/core/logging/LogService.java
package com.example.core.logging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LogService {
    // singleton instance (if you ever need .get())
    private static final LogService instance = new LogService();

    // two separate loggers for “detailed” vs “simple” output
    private static final Logger detailed = Logger.getLogger("com.example.detailed");
    private static final Logger simple   = Logger.getLogger("com.example.simple");

    // subscriber lists
    private static final List<Consumer<LogRecord>> detailedListeners = new CopyOnWriteArrayList<>();
    private static final List<Consumer<LogRecord>> simpleListeners   = new CopyOnWriteArrayList<>();

    static {
        // detach parent handlers if you don’t want console output, etc.
        detailed.setUseParentHandlers(false);
        simple  .setUseParentHandlers(false);

        // install exactly one Handler on each logger
        detailed.setLevel(Level.ALL);
        detailed.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                for (var sub : detailedListeners) sub.accept(record);
            }
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        });

        simple.setLevel(Level.ALL);
        simple.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                for (var sub : simpleListeners) sub.accept(record);
            }
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        });
    }

    private LogService() {
        // private ctor → use LogService.get() if you need the instance
    }

    public static LogService get() {
        return instance;
    }

    /** Your global “detailed” logger (use for verbose events) */
    public static Logger detailed() {
        return detailed;
    }

    /** Your global “simple” logger (use for user-facing events) */
    public static Logger simple() {
        return simple;
    }

    /** Logger for a particular class name */
    public Logger forClass(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }

    //–––––––– subscriber management ––––––––

    public static void addDetailedListener(Consumer<LogRecord> sub) {
        detailedListeners.add(sub);
    }
    public static void removeDetailedListener(Consumer<LogRecord> sub) {
        detailedListeners.remove(sub);
    }

    public static void addSimpleListener(Consumer<LogRecord> sub) {
        simpleListeners.add(sub);
    }
    public static void removeSimpleListener(Consumer<LogRecord> sub) {
        simpleListeners.remove(sub);
    }
}
