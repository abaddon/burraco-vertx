Feature: Two Players Game Start

  Scenario: Start a game with 2 players
    Given an existing game with 2 players
    When cards are requested
    Then the game status is "STARTED"
    And each player has 11 cards in hand
