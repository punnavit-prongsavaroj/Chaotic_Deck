package com.se.eternalclash2.service.engine;

public abstract class AbstractCharacterAction implements GameActionStrategy {

    @Override
    public ActionResult execute(GameContext ctx) {
        validate(ctx);
        payCost(ctx);
        if (requiresChallengeWindow()) {
            return openChallengeWindow(ctx);
        }
        return doEffect(ctx);
    }

    protected abstract void validate(GameContext ctx);
    protected abstract void payCost(GameContext ctx);
    protected abstract boolean requiresChallengeWindow();
    protected abstract ActionResult doEffect(GameContext ctx);
    
    protected ActionResult openChallengeWindow(GameContext ctx) {
        return ActionResult.builder()
                .success(true)
                .message("Action declared. Waiting for challenges.")
                .nextPhase(GamePhase.CHALLENGE_WINDOW)
                .build();
    }
}
