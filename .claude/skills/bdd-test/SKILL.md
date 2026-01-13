---
name: bdd-test
description: Phase 5 of feature development - Create BDD tests with Cucumber/Gherkin and unit tests for the Burraco system. Use after kotlin-implement to add comprehensive test coverage.
---

# BDD Testing Skill

This skill creates BDD acceptance tests and unit tests for the feature.

## Usage

```
/bdd-test <feature-name>
```

## Prerequisites

Run `/kotlin-implement` first to have the implementation ready.

## Instructions

### Step 1: Create Gherkin Feature File

Location: `e2eTest/src/test/resources/features/`

```gherkin
# File: [feature-name].feature

@[feature-tag]
Feature: [Feature Name]
  As a Burraco player
  I want to [action]
  So that [benefit]

  Background:
    Given a game with 2 players is created
    And the game has been started with cards dealt

  Scenario: Successfully [action]
    Given [precondition]
    When [action is performed]
    Then [expected result]
    And [additional verification]

  Scenario: Reject [action] when [invalid condition]
    Given [invalid precondition]
    When [action is attempted]
    Then the action should be rejected with error "[error message]"

  Scenario Outline: [Action] with various inputs
    Given [setup with <param>]
    When [action with <input>]
    Then the result should be <expected>

    Examples:
      | param | input | expected |
      | val1  | in1   | result1  |
      | val2  | in2   | result2  |
```

### Step 2: Create Step Definitions

Location: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/steps/`

```kotlin
package com.abaddon83.burraco.e2e.steps

import com.abaddon83.burraco.e2e.support.TestContext
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import java.time.Duration

class [Feature]StepDefinitions(private val context: TestContext) {

    private val httpClient get() = context.httpClient

    @Given("the current player has {string}")
    fun givenPlayerHas(condition: String) {
        val playerView = httpClient.getPlayerView(
            context.currentPlayerId!!,
            context.gameId!!
        )
        assertThat(playerView.cards).isNotEmpty()
    }

    @When("the player performs [feature] action")
    fun whenPlayerPerformsAction() {
        val request = JsonObject()
            .put("playerId", context.currentPlayerId?.toString())
            .put("param1", "value1")

        context.lastResponse = httpClient.post(
            "/games/${context.gameId}/[feature]",
            request
        )
    }

    @Then("the action should succeed")
    fun thenActionSucceeds() {
        assertThat(context.lastResponse?.statusCode())
            .isIn(200, 201, 204)
    }

    @Then("the action should be rejected with error {string}")
    fun thenActionRejectedWithError(errorMessage: String) {
        assertThat(context.lastResponse?.statusCode())
            .isIn(400, 409, 422)
        val body = context.lastResponse?.bodyAsJsonObject()
        assertThat(body?.getString("error")).contains(errorMessage)
    }

    @Then("the result should be visible in player view")
    fun thenResultVisibleInPlayerView() {
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                val playerView = httpClient.getPlayerView(
                    context.currentPlayerId!!,
                    context.gameId!!
                )
                assertThat(playerView.[featureField]).isNotNull()
            }
    }
}
```

### Step 3: Create Unit Tests

Location: `[Service]/src/test/kotlin/com/abaddon83/burraco/[service]/commands/`

```kotlin
// Happy path test
internal class Given_[ValidState]_When_[Command]_Then_[Event] :
    KcqrsAggregateTestSpecification<[Aggregate]>() {

    private val aggregateId = [Context]Identity.create()

    override fun emptyAggregate() = { [InitialState].empty() }

    override fun given(): List<IDomainEvent> = listOf(
        [PreviousEvent1].create(aggregateId, ...),
        [PreviousEvent2].create(aggregateId, ...)
    )

    override fun `when`(): ICommand<[Aggregate]> = [CommandName](
        aggregateID = aggregateId,
        param1 = "validValue"
    )

    override fun expected(): List<IDomainEvent> = listOf(
        [EventName].create(aggregateId, expectedResult)
    )

    override fun expectedException(): Class<out Exception>? = null
}

// Invalid state test
internal class Given_[InvalidState]_When_[Command]_Then_Exception :
    KcqrsAggregateTestSpecification<[Aggregate]>() {
    // ... setup for invalid state ...
    override fun expected() = emptyList<IDomainEvent>()
    override fun expectedException() = UnsupportedOperationException::class.java
}
```

### Step 4: Run Tests

```bash
# Run unit tests
./gradlew :[Service]:test --tests "*[CommandName]*"

# Run E2E tests with fresh Docker images
./gradlew :e2eTest:e2eTestClean

# Run specific feature
./gradlew :e2eTest:test -Dcucumber.filter.tags="@[feature-tag]"
```

### Step 5: Testing Checklist

```markdown
### Gherkin Feature File
- [ ] Feature file created
- [ ] Happy path scenario
- [ ] Error scenarios
- [ ] Edge cases covered

### Step Definitions
- [ ] All Given/When/Then steps implemented
- [ ] Awaitility for async assertions
- [ ] TestContext for shared state

### Unit Tests
- [ ] Happy path test
- [ ] Invalid state test
- [ ] Validation test

### Execution
- [ ] ./gradlew :[Service]:test passes
- [ ] ./gradlew :e2eTest:e2eTestClean passes
```

## Reference Files

- Feature File: `e2eTest/src/test/resources/features/game-creation.feature`
- Step Definitions: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/steps/GameStepDefinitions.kt`
- Unit Test: `Game/src/test/kotlin/com/abaddon83/burraco/game/commands/gameDraft/CreateGameTest.kt`
