package com.example.ui.app;

import com.example.core.sequence.InputSequence;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Suggestion {
    private final InputSequence pattern;
    private final StringProperty hint = new SimpleStringProperty(this, "hint", "");

    public Suggestion (InputSequence pattern, String hint) {
        this.pattern = pattern;
        this.hint.set(hint);
    }

    public InputSequence getPattern() {
        return pattern;
    }

    public StringProperty hintProperty() {
        return hint;
    }

    public String getHint() {
        return hint.get();
    }

    public void setHint(String hint) {
        this.hint.set(hint);
    }
}