package com.example.ChaoticDeck.Model.TOP3;

import com.example.ChaoticDeck.Model.Card.Card;

public class TOP3 {

    private int Number;
    private Card card;

    // ── Constructors ──────────────────────────────────────
    public TOP3() {}

    public TOP3(int number, Card card) {
        Number = number;
        this.card = card;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    
    


}