package com.se.eternalclash2.service.engine;

import com.se.eternalclash2.domain.entity.GameAction;
import com.se.eternalclash2.domain.entity.GameRoom;
import com.se.eternalclash2.domain.entity.GamePlayer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameContext {
    private GameRoom room;
    private GamePlayer actor;
    private GamePlayer target;
    private GameAction action;
}
