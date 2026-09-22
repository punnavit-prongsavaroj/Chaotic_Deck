package com.example.ChaoticDeck.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameRepository {

    private final JdbcTemplate jdbcTemplate;

    public GameRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    

    public List<Product> findAll() {

        String sql = """
                SELECT id, name, category, brand,
                       stock, price, discount_type
                FROM product
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Product product = new Product();

            product.setId(rs.getString("id"));
            product.setName(rs.getString("name"));
            product.setCategory(rs.getString("category"));
            product.setBrand(rs.getString("brand"));
            product.setStock(rs.getInt("stock"));
            product.setPrice(rs.getDouble("price"));
            product.setDiscountType(
                    rs.getString("discount_type")
            );

            return product;
        });
    }
}