package com.example.ChaoticDeck.Model.RoomData;

import com.example.ChaoticDeck.Model.BOMB.BOMB;
import com.example.ChaoticDeck.Model.DeckList.DeckList;
import com.example.ChaoticDeck.Model.TOP3.TOP3;

public class RoomData {
    private String RoomID;
    private int Top3Count;
    private TOP3 TOP3;
    private BOMB BOMB;
    private DeckList DeckList;
    // ── Constructors ──────────────────────────────────────
    public RoomData() {}

    public RoomData(String roomID, int top3Count, com.example.ChaoticDeck.Model.TOP3.TOP3 tOP3,
            com.example.ChaoticDeck.Model.BOMB.BOMB bOMB, com.example.ChaoticDeck.Model.DeckList.DeckList deckList) {
        RoomID = roomID;
        Top3Count = top3Count;
        TOP3 = tOP3;
        BOMB = bOMB;
        DeckList = deckList;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public int getTop3Count() {
        return Top3Count;
    }

    public void setTop3Count(int top3Count) {
        Top3Count = top3Count;
    }

    public TOP3 getTOP3() {
        return TOP3;
    }

    public void setTOP3(TOP3 tOP3) {
        TOP3 = tOP3;
    }

    public BOMB getBOMB() {
        return BOMB;
    }

    public void setBOMB(BOMB bOMB) {
        BOMB = bOMB;
    }

    public DeckList getDeckList() {
        return DeckList;
    }

    public void setDeckList(DeckList deckList) {
        DeckList = deckList;
    }

}