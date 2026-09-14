package com.se.eternalclash2.service.engine;

import org.springframework.stereotype.Component;

@Component("COUP")
public class CoupAction implements GameActionStrategy {

    @Override
    public ActionResult execute(GameContext ctx) {
        if (ctx.getActor().getCoins() < 7) {
            throw new IllegalStateException("Not enough coins for Coup");
        }
        ctx.getActor().setCoins(ctx.getActor().getCoins() - 7);
        // Target loses influence logic here
        return ActionResult.builder()
                .success(true)
                .message("Coup successful.")
                .nextPhase(GamePhase.RESOLUTION)
                .build();
    }
}
