package com.example.ChaoticDeck.Model.TOP3;

import com.example.ChaoticDeck.Model.Card.Card;
import java.util.List;

public class TOP3 {

    private int id;
    private int Number;
    private int top3Count;
    private List<Card> cards;

    // ── Constructors ──────────────────────────────────────
    public TOP3() {}

    public TOP3(int id, int number, int top3Count, List<Card> cards) {
        this.id = id;
        this.Number = number;
        this.top3Count = top3Count;
        this.cards = cards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public int getTop3Count() {
        return top3Count;
    }

    public void setTop3Count(int top3Count) {
        this.top3Count = top3Count;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}