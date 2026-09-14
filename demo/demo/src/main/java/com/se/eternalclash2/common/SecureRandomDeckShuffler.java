package com.se.eternalclash2.common;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Component
public class SecureRandomDeckShuffler implements DeckShuffler {
    private final SecureRandom random = new SecureRandom();

    @Override
    public <T> void shuffle(List<T> deck) {
        Collections.shuffle(deck, random);
    }
}
