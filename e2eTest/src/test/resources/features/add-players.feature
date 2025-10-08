Feature: Create Player for a Game

  Background:
    Given a valid new game request
    When the new game request is processed
    Then a new game is created

  Scenario: Associate a player to the created game
    Given a new player request for the game created with user equals "user1"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated


