# e2eTest Module

## Overview

The **e2eTest** module provides end-to-end (E2E) testing capabilities for the Burraco microservices using **Behavior-Driven Development (BDD)** with Cucumber and Gherkin syntax.

### Key Features

- **BDD Testing**: Write tests in natural language using Gherkin
- **Testcontainers Integration**: Automatically manages Docker container lifecycle
- **Full Stack Testing**: Tests entire service interactions (REST APIs, Kafka events, projections)
- **Self-Contained**: No manual infrastructure setup required
- **CI/CD Ready**: Fully automated test execution

## Scope

This module tests the Burraco system's behavior from an end-user perspective by:

1. **Testing REST APIs**: Verify HTTP endpoints return correct responses
2. **Validating Events**: Confirm Kafka events are published correctly
3. **Checking Projections**: Ensure read models are updated properly
4. **Testing Service Integration**: Validate cross-service communication

## Architecture

### Technology Stack

- **Cucumber 7.20.1**: BDD framework for writing tests in Gherkin
- **JUnit Platform**: Test execution engine
- **Testcontainers 1.21.0**: Docker container management
- **REST-assured 5.5.0**: HTTP client for API testing
- **Awaitility 4.2.2**: Async assertion library
- **Apache Kafka Client**: Event verification

### Project Structure

```
e2eTest/
├── src/test/kotlin/com/abaddon83/burraco/e2e/
│   ├── TestRunner.kt                    # Cucumber test runner
│   ├── infrastructure/
│   │   ├── ContainerManager.kt          # Manages Docker containers
│   │   └── ServiceEndpoints.kt          # Service URL configuration
│   ├── support/
│   │   ├── Hooks.kt                     # Cucumber lifecycle hooks
│   │   ├── TestContext.kt               # Shared test data
│   │   ├── HttpClient.kt                # REST API client wrapper
│   │   └── KafkaHelper.kt               # Kafka consumer for events
│   └── steps/
│       ├── GameStepDefinitions.kt       # Game-related test steps
│       └── PlayerStepDefinitions.kt     # Player-related test steps
└── src/test/resources/features/
    └── game-player-creation.feature     # Gherkin scenarios

```

### How It Works

1. **BeforeAll**: `ContainerManager` starts all services using docker-compose.yml via Testcontainers
2. **Test Execution**: Cucumber runs feature files, executing step definitions
3. **Step Definitions**: Call REST APIs via `HttpClient`, verify responses
4. **Verification**: Assert REST responses, Kafka events, and database projections
5. **AfterAll**: `ContainerManager` stops and cleans up all containers

## Running Tests

### Prerequisites

- Docker installed and running
- Services built: `./gradlew build shadowJar`

### Execute Tests

```bash
# Run E2E tests with existing Docker images (faster)
./gradlew :e2eTest:test

# Run E2E tests with FRESH Docker images (recommended for testing code changes)
./gradlew :e2eTest:e2eTestClean

# Clean only Docker images and containers (without running tests)
./gradlew :e2eTest:cleanDockerImages

# Build Docker images from scratch (without running tests)
./gradlew :e2eTest:buildDockerImages

# Run with detailed output
./gradlew :e2eTest:e2eTestClean --info

# View test report
open e2eTest/build/reports/tests/test/index.html
```

### Available Gradle Tasks

| Task | Description | When to Use |
|------|-------------|-------------|
| `:e2eTest:test` | Run E2E tests with existing images | Quick test run, no code changes |
| `:e2eTest:e2eTestClean` | **Clean + Rebuild + Test** | After code changes (RECOMMENDED) |
| `:e2eTest:cleanDockerImages` | Remove all Docker images/containers | Manual cleanup |
| `:e2eTest:buildDockerImages` | Build fresh images without testing | Verify Docker build only |
| `:e2eTest:buildServiceJars` | Build all service JARs | Verify compilation only |

### What Happens

#### Normal Test Run (`:e2eTest:test`)
1. Uses existing Docker images (if available)
2. Starts containers with `docker-compose up -d`
3. Waits for service health checks
4. Cucumber discovers and executes `.feature` files
5. Step definitions interact with services via HTTP and Kafka
6. Assertions verify expected behavior
7. Containers are automatically stopped and removed

#### Clean Test Run (`:e2eTest:e2eTestClean`) - **RECOMMENDED**
1. **Stops and removes** all running containers
2. **Deletes all Docker images** (base-builder, game, player, dealer)
3. **Rebuilds JARs** from source (`:Game:shadowJar`, `:Player:shadowJar`, etc.)
4. **Rebuilds Docker images** with `--no-cache` flag (including DockerFile-base)
5. **Starts containers** with fresh images
6. **Runs all E2E tests**
7. **Cleans up** containers after tests

### Why Use e2eTestClean?

The `e2eTestClean` task ensures you're testing the **latest version of your code** by:

✅ **Forcing full rebuild** of all Docker images (no cache)
✅ **Rebuilding base image** (DockerFile-base) with latest dependencies
✅ **Ensuring code changes** are reflected in service containers
✅ **Avoiding stale image issues** where tests pass but code is outdated
✅ **Validating entire build** from source to container

**Use this task whenever:**
- You've made code changes to Game, Player, or Dealer services
- You've updated dependencies in build.gradle.kts files
- You want to ensure tests reflect the latest codebase
- You're preparing for a deployment or PR review

## Writing New Tests

### 1. Create a Feature File

Create `e2eTest/src/test/resources/features/my-feature.feature`:

```gherkin
Feature: My New Feature
  Scenario: Test scenario description
    Given a precondition
    When an action occurs
    Then verify the outcome
```

### 2. Implement Step Definitions

Create `e2eTest/src/test/kotlin/.../steps/MyStepDefinitions.kt`:

```kotlin
class MyStepDefinitions {
    private val context = TestContext.get()

    @Given("a precondition")
    fun aPrecondition() {
        // Setup test data
    }

    @When("an action occurs")
    fun anActionOccurs() {
        val response = HttpClient.createGame()
        context.lastResponse = response
    }

    @Then("verify the outcome")
    fun verifyTheOutcome() {
        assertThat(context.lastResponse?.statusCode(), equalTo(200))
    }
}
```

### 3. Use Test Context

Share data between steps using `TestContext`:

```kotlin
context.gameId = "some-id"          // Store
val gameId = context.gameId         // Retrieve
context.lastResponse = response     // Store HTTP response
```

### 4. Call Services

```kotlin
// REST APIs
val response = HttpClient.createGame()
val response = HttpClient.createPlayer(gameId, "user1")

// Kafka Events
val kafkaHelper = KafkaHelper.create()
kafkaHelper.subscribe("game-events")
val messages = kafkaHelper.pollMessages(Duration.ofSeconds(10))
```

## Test Scenarios

### Current Tests

#### Game and Player Creation
- **File**: `game-player-creation.feature`
- **Tests**:
  - Create a new Burraco game
  - Create a player for the game
  - Associate player with game
  - Verify game has player associated

### Future Tests

- Game initialization with card dealing
- Player turn management
- Card play validation
- Game termination
- Error handling scenarios
- Concurrent player actions

## Configuration

### Service Endpoints

Testcontainers dynamically assigns ports. `ServiceEndpoints` stores actual URLs:

```kotlin
val gameUrl = ServiceEndpoints.get().gameServiceUrl      // http://localhost:xxxxx
val playerUrl = ServiceEndpoints.get().playerServiceUrl  // http://localhost:yyyyy
val kafkaBroker = ServiceEndpoints.get().kafkaBroker     // localhost:zzzzz
```

### Timeouts

Configured in `build.gradle.kts`:

```kotlin
tasks.test {
    timeout.set(Duration.ofMinutes(15))  // Allow time for container startup
}
```

### Container Lifecycle

Controlled by `ContainerManager`:

```kotlin
ContainerManager.start()  // Start all services
ContainerManager.stop()   // Stop and cleanup
```

## Debugging

### View Container Logs

While tests are running, containers are active. Check logs:

```bash
docker ps                          # Find container IDs
docker logs <container-id>         # View logs
```

### Test Reports

HTML reports generated at: `e2eTest/build/reports/tests/test/index.html`

### Enable Debug Logging

Add to test JVM args in `build.gradle.kts`:

```kotlin
tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
```

## Best Practices

1. **Keep Scenarios Focused**: One scenario per business behavior
2. **Use Descriptive Names**: Feature and scenario names should be self-explanatory
3. **Reuse Step Definitions**: Don't duplicate steps, parameterize them
4. **Clean Test Data**: Use `TestContext.clear()` between scenarios
5. **Async Assertions**: Use Awaitility for eventual consistency
6. **Verify Events**: Don't just test APIs, verify events were published
7. **Test Failures**: Include negative test scenarios

## Known Limitations

- Testcontainers requires Docker (no Windows containers support)
- Container startup adds ~2-5 minutes to test execution
- docker-compose.yml cannot use `container_name` property
- Tests run sequentially (no parallel execution yet)

## Troubleshooting

### Docker Not Running
**Error**: "Cannot connect to Docker daemon"
**Solution**: Start Docker Desktop

### Port Conflicts
**Error**: "Port already in use"
**Solution**: Testcontainers uses dynamic ports, no conflicts should occur

### Container Startup Timeout
**Error**: "Wait strategy timeout"
**Solution**: Increase timeout in `ContainerManager.kt` wait strategies

### Build Failures
**Error**: Service images not found
**Solution**: Run `./gradlew build shadowJar` before tests

## Contributing

When adding new tests:

1. Write feature file in Gherkin
2. Implement step definitions
3. Update this documentation
4. Ensure tests pass: `./gradlew :e2eTest:test`
5. Commit both feature and step definitions together

## References

- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Testcontainers Documentation](https://testcontainers.com/)
- [REST-assured Documentation](https://rest-assured.io/)
- [Gherkin Syntax](https://cucumber.io/docs/gherkin/reference/)

## Generic rules to apply always ##
- always think hard and produce a plan before execute any change
- Always test any change