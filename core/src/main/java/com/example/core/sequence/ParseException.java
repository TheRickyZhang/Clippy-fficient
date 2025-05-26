package com.example.core.sequence;

import java.util.List;

public class ParseException extends Exception {
    public List<String> messages;
    public ParseException(List<String> messages) {
        this.messages = messages;
    }
}
