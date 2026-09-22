package com.example.ChaoticDeck.Model.Player;

import com.example.ChaoticDeck.Model.HandCard.HandCard;

public class Player {

    private String name;
    private HandCard handcard;

    // ── Constructors ──────────────────────────────────────
    public Player() {}

    public Player(String name, HandCard handcard) {
        this.name = name;
        this.handcard = handcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HandCard getHandcard() {
        return handcard;
    }

    public void setHandcard(HandCard handcard) {
        this.handcard = handcard;
    }

    


}