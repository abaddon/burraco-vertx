Feature: Pick Up Card from Deck

  Scenario: Player picks up a card from the deck during their turn
    Given an existing game with 2 players
    And cards are requested
    When the current player picks up a card from the deck
    Then the pick up request is successful
    And the response contains card details

  Scenario: Wrong player tries to pick up a card
    Given an existing game with 2 players
    And cards are requested
    When the wrong player tries to pick up a card from the deck
    Then the pick up request fails with forbidden status

  Scenario: Player tries to pick up a card from an empty deck
    Given an existing game with 2 players
    And cards are requested
    And all cards are drawn from the deck
    When the current player picks up a card from the deck
    Then the pick up request fails with conflict status
