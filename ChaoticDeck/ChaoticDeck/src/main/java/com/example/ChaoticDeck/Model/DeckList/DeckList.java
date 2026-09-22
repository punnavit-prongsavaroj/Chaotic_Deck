package com.example.ChaoticDeck.Model.DeckList;

import com.example.ChaoticDeck.Model.Card.Card;

public class DeckList {

    private Card card;


    // ── Constructors ──────────────────────────────────────
    public DeckList() {}

    public DeckList(Card card) {
        this.card = card;
    }
    // ── Getters & Setters ─────────────────────────────────


    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
    
}