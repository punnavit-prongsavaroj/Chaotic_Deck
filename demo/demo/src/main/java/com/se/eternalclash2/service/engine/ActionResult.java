package com.se.eternalclash2.service.engine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResult {
    private boolean success;
    private String message;
    private GamePhase nextPhase;
}
