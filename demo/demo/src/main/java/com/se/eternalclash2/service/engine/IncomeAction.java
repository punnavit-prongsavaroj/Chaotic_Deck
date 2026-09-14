package com.se.eternalclash2.service.engine;

import org.springframework.stereotype.Component;

@Component("INCOME")
public class IncomeAction implements GameActionStrategy {

    @Override
    public ActionResult execute(GameContext ctx) {
        // Income is basic, no challenge, no block.
        ctx.getActor().setCoins(ctx.getActor().getCoins() + 1);
        return ActionResult.builder()
                .success(true)
                .message("Income collected.")
                .nextPhase(GamePhase.RESOLUTION)
                .build();
    }
}
