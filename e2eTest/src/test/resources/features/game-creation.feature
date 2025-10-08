Feature: Game Creation

  Scenario: Create a new game and associate a player
    Given a valid new game request
    When the new game request is processed
    Then a new game is created
