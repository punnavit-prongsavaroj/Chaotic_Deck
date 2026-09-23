CREATE TABLE IF NOT EXISTS room (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(50) UNIQUE NOT NULL,
    leader_id INT NOT NULL,
    top3_count INT DEFAULT 0,
    turn_count INT DEFAULT 0,
    required_draws INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS player (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS card (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS hand_card (
    id SERIAL PRIMARY KEY,
    player_id INT NOT NULL,
    card_id INT NOT NULL,
    amount INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS bomb (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    bomb_count INT DEFAULT -1
);

CREATE TABLE IF NOT EXISTS decklist (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    card_id INT NOT NULL,
    amount INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS top3 (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    number INT NOT NULL,
    top3_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS player_in_room (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    player_id INT NOT NULL
);
