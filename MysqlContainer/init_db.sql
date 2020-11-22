CREATE DATABASE IF NOT EXISTS eventstore;
SET NAMES utf8mb4;
CREATE TABLE IF NOT EXISTS eventstore.event (
  `name` varchar(50) DEFAULT NULL,
  `entity_key` varchar(50) NOT NULL,
  `entity_name` varchar(50) NOT NULL,
  `instant` timestamp NULL DEFAULT NULL,
  `json_payload` text,
  KEY `entity_key` (`entity_key`,`entity_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS game;
SET NAMES utf8mb4;
CREATE TABLE IF NOT EXISTS game.game (
  `id` varchar(50) NOT NULL,
  `status` varchar(50) NOT NULL,
  `deck` text,
  `players` text,
  `player_turn` varchar(50) NULL DEFAULT NULL,
  `num_mazzetto_available` text,
  `discard_pile` text,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS game.game_player (
  `id` varchar(50) NOT NULL,
  `game_id` varchar(50) NOT NULL,
  `hand_cards` text,
  `tris` text,
  `scale` text,
  CONSTRAINT PK_game_player PRIMARY KEY (id,game_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;