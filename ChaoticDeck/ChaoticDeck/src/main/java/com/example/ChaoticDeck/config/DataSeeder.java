package com.example.ChaoticDeck.config;

import com.example.ChaoticDeck.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CardRepository cardRepository;

    public DataSeeder(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (cardRepository.count() == 0) {
            System.out.println("Seeding basic cards into the database...");
            
            // 1: BOMB, 2: DEFUSE
            cardRepository.add(1, "BOMB", "Exploding Kitten");
            cardRepository.add(2, "DEFUSE", "Defuse");
            
            // Action Cards (3-6)
            cardRepository.add(3, "SKIP", "Skip");
            cardRepository.add(4, "ATTACK", "Attack");
            cardRepository.add(5, "SHUFFLE", "Shuffle");
            cardRepository.add(6, "SEETHEFUTURE", "See The Future");
            
            // Normal / Combo Cards (7-11)
            cardRepository.add(7, "NORMAL", "Cattermelon");
            cardRepository.add(8, "NORMAL", "Beard Cat");
            cardRepository.add(9, "NORMAL", "Tacocat");
            cardRepository.add(10, "NORMAL", "Hairy Potato Cat");
            cardRepository.add(11, "NORMAL", "Rainbow-Ralphing Cat");
            
            // Favor / Nope (If implemented later)
            cardRepository.add(12, "FAVOR", "Favor");
            
            System.out.println("Cards seeded successfully.");
        }
    }
}
