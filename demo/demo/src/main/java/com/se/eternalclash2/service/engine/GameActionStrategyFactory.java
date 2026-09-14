package com.se.eternalclash2.service.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class GameActionStrategyFactory {

    private final Map<String, GameActionStrategy> strategies;

    @Autowired
    public GameActionStrategyFactory(Map<String, GameActionStrategy> strategies) {
        this.strategies = strategies;
    }

    public GameActionStrategy getStrategy(String actionType) {
        GameActionStrategy strategy = strategies.get(actionType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown action type: " + actionType);
        }
        return strategy;
    }
}
