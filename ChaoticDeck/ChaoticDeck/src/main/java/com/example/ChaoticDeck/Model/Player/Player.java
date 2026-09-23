package com.example.ChaoticDeck.Model.Player;

import com.example.ChaoticDeck.Model.HandCard.HandCard;
import java.util.List;

public class Player {

    private long id;
    private String name;
    private List<HandCard> handCards;

    // ── Constructors ──────────────────────────────────────
    public Player() {}

    public Player(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(long id, String name, List<HandCard> handCards) {
        this.id = id;
        this.name = name;
        this.handCards = handCards;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HandCard> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<HandCard> handCards) {
        this.handCards = handCards;
    }
}