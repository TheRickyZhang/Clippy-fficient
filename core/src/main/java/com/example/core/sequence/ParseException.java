package com.example.core.sequence;

import java.util.List;

public class ParseException extends Exception {
    public List<String> messages;
    public ParseException(List<String> messages) {
        this.messages = messages;
    }
    @Override
    public String toString () {
        return String.join("", messages);
    }
}
