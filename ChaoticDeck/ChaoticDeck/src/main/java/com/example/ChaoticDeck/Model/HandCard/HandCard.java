package com.example.ChaoticDeck.Model.HandCard;

import com.example.ChaoticDeck.Model.Card.Card;

public class HandCard {

    private Card card;
    private int Amount;

    // ── Constructors ──────────────────────────────────────
    public HandCard() {}

    
    public HandCard(Card card, int amount) {
        this.card = card;
        Amount = amount;
    }


    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }


    
}