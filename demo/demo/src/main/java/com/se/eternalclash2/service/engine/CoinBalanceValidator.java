package com.se.eternalclash2.service.engine;

public class CoinBalanceValidator extends ActionValidator {
    private final int requiredCoins;

    public CoinBalanceValidator(int requiredCoins) {
        this.requiredCoins = requiredCoins;
    }

    @Override
    protected void doValidate(GameContext ctx) {
        if (ctx.getActor().getCoins() < requiredCoins) {
            throw new IllegalStateException("Insufficient coins to perform action");
        }
    }
}
