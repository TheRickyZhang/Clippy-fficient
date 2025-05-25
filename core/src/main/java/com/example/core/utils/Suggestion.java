package com.example.core.utils;

import com.example.core.sequence.InputSequence;

/**
 * Core only knows: sequence → hint → actionType
 */
public class Suggestion {
    private InputSequence sequence;
    private String tip;
    private AppAction actionType;

    public Suggestion(InputSequence seq, String tip, AppAction type) {
        this.sequence = seq;
        this.tip = tip;
        this.actionType = type;
    }

    public InputSequence getSequence() {
        return sequence;
    }
    public void setSequence(InputSequence s) {
        sequence = s;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String t) {
        tip = t;
    }

    public AppAction getActionType() {
        return actionType;
    }

    public void setActionType(AppAction t) {
        actionType = t;
    }
}
