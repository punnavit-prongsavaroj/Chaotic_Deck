package com.se.eternalclash2.service;

import org.springframework.stereotype.Service;

@Service
public class GameFacadeService {
    // Constructor Injection (Dependency Inversion Principle)
    private final RoomReadService roomReadService;
    private final RoomWriteService roomWriteService;

    public GameFacadeService(RoomReadService roomReadService, RoomWriteService roomWriteService) {
        this.roomReadService = roomReadService;
        this.roomWriteService = roomWriteService;
    }

    public void playTurn(String actionType) {
        // Orchestrates the flow using services, validators, and strategies
    }
}
