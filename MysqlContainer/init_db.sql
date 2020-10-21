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