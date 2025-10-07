package com.abaddon83.burraco.e2e.support

import com.abaddon83.burraco.e2e.infrastructure.ContainerManager
import io.cucumber.java.AfterAll
import io.cucumber.java.Before
import io.cucumber.java.BeforeAll

/**
 * Cucumber hooks to manage container lifecycle and test setup.
 */
class Hooks {
    @Before
    fun beforeScenario() {
        // Clear test context before each scenario
        TestContext.clear()
    }
}

/**
 * Static hooks for container lifecycle management.
 * Must be top-level functions for Cucumber to recognize them properly.
 */
@BeforeAll
fun startContainers() {
    ContainerManager.start()
}

@AfterAll
fun stopContainers() {
    ContainerManager.stop()
}
