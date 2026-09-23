package com.example.ChaoticDeck.Model.DeckList;

import com.example.ChaoticDeck.Model.Card.Card;
import java.util.List;

public class DeckList {

    private int id;
    private List<Card> cards;

    // ── Constructors ──────────────────────────────────────
    public DeckList() {}

    public DeckList(int id, List<Card> cards) {
        this.id = id;
        this.cards = cards;
    }

    public DeckList(List<Card> cards) {
        this.cards = cards;
    }

    // ── Getters & Setters ─────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}