Feature: Two Players Game Start

  Scenario: Start a game with 2 players
    Given an existing game with 2 players
    When the game is started
    Then the game status is "STARTED"
    And the game has 2 players associated
