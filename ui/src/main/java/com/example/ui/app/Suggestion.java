package com.example.ui.app;

import com.example.core.sequence.InputSequence;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Suggestion {
    private final InputSequence pattern;
    private final StringProperty tip = new SimpleStringProperty(this, "tip", "");

    public Suggestion(InputSequence pattern, String tip) {
        this.pattern = pattern;
        this.tip.set(tip);
    }

    public InputSequence getPattern() { return pattern; }

    public StringProperty tipProperty() { return tip; }
    public String getTip()             { return tip.get(); }
    public void setTip(String tip)     { this.tip.set(tip); }
}
