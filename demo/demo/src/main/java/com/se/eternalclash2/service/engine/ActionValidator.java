package com.se.eternalclash2.service.engine;

public abstract class ActionValidator {
    private ActionValidator next;

    public void setNext(ActionValidator next) {
        this.next = next;
    }

    public void validate(GameContext ctx) {
        doValidate(ctx);
        if (next != null) {
            next.validate(ctx);
        }
    }

    protected abstract void doValidate(GameContext ctx);
}
