package com.example.ChaoticDeck.Model.DiscardPile;

import java.sql.Timestamp;

public class DiscardPile {
    private int id;
    private String roomId;
    private int cardId;
    private long playedBy;
    private Timestamp playedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
    public long getPlayedBy() { return playedBy; }
    public void setPlayedBy(long playedBy) { this.playedBy = playedBy; }
    public Timestamp getPlayedAt() { return playedAt; }
    public void setPlayedAt(Timestamp playedAt) { this.playedAt = playedAt; }
}
