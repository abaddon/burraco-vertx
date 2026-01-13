# BDD Testing Skill

**Skill**: `/bdd-test`
**Purpose**: Phase 5 - Create BDD tests with Cucumber/Gherkin and unit tests

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

  # Happy path scenario
  Scenario: Successfully [action]
    Given [precondition]
    When [action is performed]
    Then [expected result]
    And [additional verification]

  # Validation scenario
  Scenario: Reject [action] when [invalid condition]
    Given [invalid precondition]
    When [action is attempted]
    Then the action should be rejected with error "[error message]"
    And [state remains unchanged]

  # Edge case scenario
  Scenario: [Edge case description]
    Given [edge case setup]
    When [action is performed]
    Then [edge case result]

  # Parameterized scenario
  Scenario Outline: [Action] with various inputs
    Given [setup with <param>]
    When [action with <input>]
    Then the result should be <expected>

    Examples:
      | param | input | expected |
      | val1  | in1   | result1  |
      | val2  | in2   | result2  |
      | val3  | in3   | result3  |
```

### Step 2: Create Step Definitions

Location: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/steps/`

```kotlin
// File: [Feature]StepDefinitions.kt
package com.abaddon83.burraco.e2e.steps

import com.abaddon83.burraco.e2e.support.TestContext
import com.abaddon83.burraco.e2e.support.HttpClient
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import io.vertx.core.json.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import java.time.Duration

class [Feature]StepDefinitions(private val context: TestContext) {

    private val httpClient: HttpClient get() = context.httpClient

    // ============ GIVEN STEPS ============

    @Given("the current player has {string}")
    fun givenPlayerHas(condition: String) {
        // Setup the required condition
        val playerView = httpClient.getPlayerView(
            context.currentPlayerId!!,
            context.gameId!!
        )

        // Verify precondition
        when (condition) {
            "required cards" -> assertThat(playerView.cards).isNotEmpty()
            "no cards" -> assertThat(playerView.cards).isEmpty()
            else -> throw IllegalArgumentException("Unknown condition: $condition")
        }
    }

    @Given("the game is in {string} phase")
    fun givenGameInPhase(phase: String) {
        // Wait for game to be in expected phase
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                val gameView = httpClient.getGameView(context.gameId!!)
                assertThat(gameView.phase).isEqualTo(phase)
            }
    }

    // ============ WHEN STEPS ============

    @When("the player performs [feature] action")
    fun whenPlayerPerformsAction() {
        val request = JsonObject()
            .put("playerId", context.currentPlayerId?.toString())
            .put("param1", "value1")
            .put("param2", 42)

        context.lastResponse = httpClient.post(
            "/games/${context.gameId}/[feature]",
            request
        )
    }

    @When("the player performs [feature] action with {string}")
    fun whenPlayerPerformsActionWith(param: String) {
        val request = JsonObject()
            .put("playerId", context.currentPlayerId?.toString())
            .put("param", param)

        context.lastResponse = httpClient.post(
            "/games/${context.gameId}/[feature]",
            request
        )
    }

    // ============ THEN STEPS ============

    @Then("the action should succeed")
    fun thenActionSucceeds() {
        assertThat(context.lastResponse?.statusCode())
            .describedAs("Expected success status code")
            .isIn(200, 201, 204)
    }

    @Then("the action should be rejected with error {string}")
    fun thenActionRejectedWithError(errorMessage: String) {
        assertThat(context.lastResponse?.statusCode())
            .describedAs("Expected error status code")
            .isIn(400, 409, 422)

        val body = context.lastResponse?.bodyAsJsonObject()
        assertThat(body?.getString("error"))
            .contains(errorMessage)
    }

    @Then("the [result] should be visible in player view")
    fun thenResultVisibleInPlayerView() {
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted {
                val playerView = httpClient.getPlayerView(
                    context.currentPlayerId!!,
                    context.gameId!!
                )
                // Verify the expected result
                assertThat(playerView.[featureField]).isNotNull()
            }
    }

    @Then("the game state should reflect [change]")
    fun thenGameStateReflectsChange() {
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted {
                val gameView = httpClient.getGameView(context.gameId!!)
                // Verify game state changed
                assertThat(gameView.[stateField]).isEqualTo("[expectedValue]")
            }
    }
}
```

### Step 3: Update TestContext (if needed)

Location: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/support/TestContext.kt`

```kotlin
// Add new fields if the feature requires shared state
class TestContext {
    // ... existing fields ...

    // New fields for this feature
    var [featureData]: [FeatureType]? = null
}
```

### Step 4: Create Unit Tests

Location: `[Service]/src/test/kotlin/com/abaddon83/burraco/[service]/commands/`

```kotlin
// File: [CommandName]Test.kt
package com.abaddon83.burraco.[service].commands.[state]

import com.abaddon83.burraco.common.models.[Context]Identity
import com.abaddon83.burraco.common.models.event.[context].[EventName]
import com.abaddon83.burraco.[service].models.[aggregate].[Aggregate]
import com.abaddon83.kcqrs.core.domain.IDomainEvent
import com.abaddon83.kcqrs.core.domain.commands.ICommand
import com.abaddon83.kcqrs.test.KcqrsAggregateTestSpecification

// Happy path test
internal class Given_[ValidState]_When_[Command]_Then_[Event] :
    KcqrsAggregateTestSpecification<[Aggregate]>() {

    private val aggregateId = [Context]Identity.create()

    override val aggregateId: [Context]Identity get() = this.aggregateId

    override fun emptyAggregate(): () -> [Aggregate] = { [InitialState].empty() }

    override fun given(): List<IDomainEvent> = listOf(
        // Events to set up the valid state
        [PreviousEvent1].create(aggregateId, ...),
        [PreviousEvent2].create(aggregateId, ...)
    )

    override fun `when`(): ICommand<[Aggregate]> = [CommandName](
        aggregateID = aggregateId,
        param1 = "validValue",
        param2 = 42
    )

    override fun expected(): List<IDomainEvent> = listOf(
        [EventName].create(aggregateId, expectedResult)
    )

    override fun expectedException(): Class<out Exception>? = null
}

// Invalid state test
internal class Given_[InvalidState]_When_[Command]_Then_Exception :
    KcqrsAggregateTestSpecification<[Aggregate]>() {

    private val aggregateId = [Context]Identity.create()

    override val aggregateId: [Context]Identity get() = this.aggregateId

    override fun emptyAggregate(): () -> [Aggregate] = { [InitialState].empty() }

    override fun given(): List<IDomainEvent> = listOf(
        // Events that create an invalid state for this command
        [SetupEvent].create(aggregateId)
        // NOT the events that would make the state valid
    )

    override fun `when`(): ICommand<[Aggregate]> = [CommandName](
        aggregateID = aggregateId,
        param1 = "value",
        param2 = 42
    )

    override fun expected(): List<IDomainEvent> = emptyList() // No events

    override fun expectedException(): Class<out Exception>? =
        UnsupportedOperationException::class.java
}

// Validation failure test
internal class Given_[ValidState]_When_[Command]WithInvalidParams_Then_Exception :
    KcqrsAggregateTestSpecification<[Aggregate]>() {

    private val aggregateId = [Context]Identity.create()

    override val aggregateId: [Context]Identity get() = this.aggregateId

    override fun emptyAggregate(): () -> [Aggregate] = { [InitialState].empty() }

    override fun given(): List<IDomainEvent> = listOf(
        // Valid state setup
        [SetupEvent].create(aggregateId, ...)
    )

    override fun `when`(): ICommand<[Aggregate]> = [CommandName](
        aggregateID = aggregateId,
        param1 = "", // Invalid: empty
        param2 = -1  // Invalid: negative
    )

    override fun expected(): List<IDomainEvent> = emptyList()

    override fun expectedException(): Class<out Exception>? =
        IllegalArgumentException::class.java
}
```

### Step 5: Run Tests

```bash
# Run unit tests for specific module
./gradlew :[Service]:test --tests "*[CommandName]*"

# Run all unit tests
./gradlew test

# Run E2E tests with fresh Docker images (recommended)
./gradlew :e2eTest:e2eTestClean

# Run E2E tests with existing images
./gradlew :e2eTest:test

# Run specific feature only
./gradlew :e2eTest:test -Dcucumber.filter.tags="@[feature-tag]"

# Run with verbose output
./gradlew :e2eTest:test --info
```

### Step 6: Testing Checklist

```markdown
## Testing Verification

### Gherkin Feature File
- [ ] Feature file created in `e2eTest/src/test/resources/features/`
- [ ] Background sets up required context
- [ ] Happy path scenario included
- [ ] Error/validation scenarios included
- [ ] Edge cases covered
- [ ] Parameterized scenarios where applicable

### Step Definitions
- [ ] All Given steps implemented
- [ ] All When steps implemented
- [ ] All Then steps implemented
- [ ] Proper use of Awaitility for async assertions
- [ ] TestContext used for shared state
- [ ] HttpClient used for REST calls

### Unit Tests
- [ ] Happy path test (valid state, valid command, expected event)
- [ ] Invalid state test (wrong state, UnsupportedOperationException)
- [ ] Validation test (valid state, invalid params, IllegalArgumentException)
- [ ] Edge case tests

### Test Execution
- [ ] ./gradlew :[Service]:test passes
- [ ] ./gradlew :e2eTest:e2eTestClean passes
- [ ] All scenarios green in Cucumber report
```

### Step 7: Debug Failed Tests

```bash
# Check service logs in Docker
docker-compose logs -f game
docker-compose logs -f player
docker-compose logs -f dealer

# Check Kafka events
# Use Redpanda Console at http://localhost:8181

# Run single test with debug
./gradlew :e2eTest:test --tests "*[TestName]*" --debug

# Check test report
open e2eTest/build/reports/tests/test/index.html
```

## Reference Files

### Feature File Example
Read: `e2eTest/src/test/resources/features/game-creation.feature`

### Step Definitions Example
Read: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/steps/GameStepDefinitions.kt`

### Unit Test Example
Read: `Game/src/test/kotlin/com/abaddon83/burraco/game/commands/gameDraft/CreateGameTest.kt`

### Test Context
Read: `e2eTest/src/test/kotlin/com/abaddon83/burraco/e2e/support/TestContext.kt`

## Gherkin Best Practices

### Use Declarative Steps
```gherkin
# Good - describes intent
Given a player with valid cards for a Tris

# Bad - too technical
Given the player hand contains Card(HEARTS, TEN), Card(SPADES, TEN), Card(DIAMONDS, TEN)
```

### Keep Scenarios Independent
```gherkin
# Good - each scenario sets up its own context
Scenario: Place a Tris
  Given a game in play phase
  Given the player has cards for a valid Tris
  When the player places the Tris
  Then the Tris should be on the table
```

### Use Tags Effectively
```gherkin
@smoke          # Quick sanity tests
@regression     # Full regression suite
@feature-name   # Feature-specific tests
@wip            # Work in progress (skip in CI)
```
