package com.example.core.utils;

import com.example.core.sequence.InputSequence;

/**
 * Core only knows: sequence → hint → actionType
 */
public class Suggestion {
    private InputSequence sequence;
    private String tip;
    private AppActionType actionType;

    public Suggestion(InputSequence seq, String tip, AppActionType type) {
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

    public AppActionType getActionType() {
        return actionType;
    }

    public void setActionType(AppActionType t) {
        actionType = t;
    }
}
