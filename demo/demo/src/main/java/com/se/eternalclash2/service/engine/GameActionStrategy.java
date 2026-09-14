package com.se.eternalclash2.service.engine;

import com.se.eternalclash2.domain.entity.GameAction;
import com.se.eternalclash2.domain.entity.GameRoom;

public interface GameActionStrategy {
    ActionResult execute(GameContext ctx);
}
