Feature: Pick Up Cards from Discard Pile

  Scenario: Player picks up all cards from the discard pile during their turn
    Given an existing game with 2 players
    And cards are requested
    When the current player picks up cards from the discard pile
    Then the pick up cards request is successful
    And the response contains a list of cards

  Scenario: Wrong player tries to pick up cards from discard pile
    Given an existing game with 2 players
    And cards are requested
    When the wrong player tries to pick up cards from the discard pile
    Then the pick up cards request fails with forbidden status

  Scenario: Player tries to pick up cards from an empty discard pile
    Given an existing game with 2 players
    And cards are requested
    And the discard pile is empty
    When the current player picks up cards from the discard pile
    Then the pick up cards request fails with conflict status
