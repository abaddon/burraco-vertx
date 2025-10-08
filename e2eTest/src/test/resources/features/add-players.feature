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

  Scenario: Associate two player to the created game
    Given a new player request for the game created with user equals "user1"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user2"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    And the game has 2 players associated

  Scenario: Associate three player to the created game
    Given a new player request for the game created with user equals "user1"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user2"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user3"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    And the game has 3 players associated

  Scenario: Associate four player to the created game
    Given a new player request for the game created with user equals "user1"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user2"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user3"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user4"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    And the game has 4 players associated

  Scenario: Try to Associate five player to the created game and fail
    Given a new player request for the game created with user equals "user1"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user2"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user3"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user4"
    When the new player request is processed
    Then a new player is associated to the game
    And the game has a player associated

    Given a new player request for the game created with user equals "user5"
    When the new player request is processed
    Then the new player is not associated to the game because the game is full

    And the game has 4 players associated


