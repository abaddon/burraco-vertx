# E2E Testing with Fresh Docker Images

This document explains how to run E2E tests with guaranteed fresh Docker images.

## Quick Start

### Test with Latest Code (Recommended)

```bash
# This command will:
# 1. Clean all existing Docker images
# 2. Rebuild JARs from source
# 3. Build fresh Docker images (including base image)
# 4. Run all E2E tests

./gradlew :e2eTest:e2eTestClean
```

### Fast Test Run (Use Existing Images)

```bash
# Only use this if you know Docker images are up-to-date
./gradlew :e2eTest:test
```

## Available Commands

| Command | What It Does | Duration | When to Use |
|---------|--------------|----------|-------------|
| `./gradlew :e2eTest:e2eTestClean` | Clean + Rebuild + Test | ~8-12 min | **After any code change** |
| `./gradlew :e2eTest:test` | Test with existing images | ~3-5 min | Quick verification only |
| `./gradlew :e2eTest:cleanDockerImages` | Remove all images/containers | ~30 sec | Manual cleanup |
| `./gradlew :e2eTest:buildDockerImages` | Rebuild images only | ~6-8 min | Verify Docker build |
| `./gradlew :e2eTest:buildServiceJars` | Build JARs only | ~2-3 min | Verify compilation |

## Why Fresh Images Matter

### The Problem

Docker caches build layers. Without explicit cleanup:
- ❌ Tests might use **old code** even after changes
- ❌ **Base image** (DockerFile-base) might be stale
- ❌ **Dependencies** might not be updated
- ❌ Tests pass but **deployed code fails**

### The Solution: `e2eTestClean`

This task guarantees fresh builds by:
1. **Removing all images** (including `base-builder`)
2. **Rebuilding with `--no-cache`** flag
3. **Rebuilding DockerFile-base** from scratch
4. **Ensuring latest code** in all containers

## Workflow Examples

### Daily Development

```bash
# Make code changes to Player service
vim Player/src/main/kotlin/...

# Test with fresh images
./gradlew :e2eTest:e2eTestClean

# Fast feedback if no changes
./gradlew :e2eTest:test
```

### Before Pull Request

```bash
# Ensure all tests pass with latest code
./gradlew :e2eTest:e2eTestClean

# Verify the results
open e2eTest/build/reports/tests/test/index.html
```

### CI/CD Pipeline

```yaml
# Always use clean build in CI
- name: Run E2E Tests
  run: ./gradlew :e2eTest:e2eTestClean
```

### Manual Cleanup

```bash
# If Docker is acting strange, clean everything
./gradlew :e2eTest:cleanDockerImages

# Verify Docker is clean
docker images | grep burraco
docker ps -a | grep burraco
```

## What Gets Cleaned

The `cleanDockerImages` task removes:

- ✅ **Service images**: `burraco-vertx-game`, `burraco-vertx-player`, `burraco-vertx-dealer`
- ✅ **Base image**: `base-builder`, `burraco-vertx-base-builder`
- ✅ **Containers**: All running/stopped containers
- ✅ **Volumes**: All named volumes (to clean databases)
- ✅ **Orphans**: Containers not defined in docker-compose.yml

Infrastructure images are **NOT** removed:
- ℹ️ EventStore (KurrentDB)
- ℹ️ Kafka (Redpanda)
- ℹ️ Redpanda Console

## Troubleshooting

### "Image not found" errors during cleanup
**This is normal!** The cleanup task tries to remove all possible images. If an image doesn't exist, it's safely ignored.

### Tests fail after cleanup
Check that services built correctly:
```bash
./gradlew :e2eTest:buildServiceJars
./gradlew :Game:shadowJar --info
```

### Docker out of space
Clean all Docker resources:
```bash
./gradlew :e2eTest:cleanDockerImages
docker system prune -a --volumes -f
```

### Containers not stopping
Manually stop everything:
```bash
cd /path/to/project
docker compose down --volumes --remove-orphans
```

## Understanding the Build Process

### 1. Clean Phase
```
🧹 Cleaning Docker environment...
   Stopping containers...
   Removing Docker images...
✅ Docker environment cleaned!
```

### 2. Build Phase
```
🐳 Building Docker images from scratch...
   This may take several minutes...

Building base-builder...  [LONG - 3-5 min]
Building game...          [FAST - uses base-builder]
Building player...        [FAST - uses base-builder]
Building dealer...        [FAST - uses base-builder]

✅ All Docker images built successfully!
```

### 3. Test Phase
```
🧪 Starting E2E tests with fresh Docker images...
🚀 Starting Burraco services with docker-compose...
⏳ Waiting for services to be ready...
   ✓ Game is healthy
   ✓ Player is healthy
✅ All services started successfully!

[Cucumber tests run]

🛑 Stopping Burraco services...
✅ All services stopped.
```

## Time Estimates

| Task | First Run | Subsequent Runs |
|------|-----------|-----------------|
| Clean | 30 seconds | 30 seconds |
| Build JARs | 2-3 minutes | 30 seconds (cached) |
| Build Base Image | 3-5 minutes | N/A (rebuilt each time) |
| Build Service Images | 1-2 minutes | N/A (rebuilt each time) |
| Start Containers | 30 seconds | 30 seconds |
| Run Tests | 1-2 minutes | 1-2 minutes |
| **Total** | **8-12 minutes** | **8-12 minutes** |

## Best Practices

### ✅ DO

- Run `e2eTestClean` after making code changes
- Run `e2eTestClean` before submitting PRs
- Use `e2eTestClean` in CI/CD pipelines
- Clean up Docker regularly to save disk space

### ❌ DON'T

- Don't use regular `test` task for important validations
- Don't assume Docker images are up-to-date
- Don't skip cleanup in CI/CD
- Don't commit without running `e2eTestClean`

## Further Reading

- See `e2eTest/CLAUDE.md` for detailed E2E test documentation
- See `docker-compose.yml` for service configuration
- See `DockerFile-base` for base image details

## Questions?

If tests behave unexpectedly:
1. Run `./gradlew :e2eTest:cleanDockerImages`
2. Run `./gradlew :e2eTest:e2eTestClean`
3. Check Docker logs: `docker logs <container-id>`
4. Verify images were rebuilt: `docker images | grep burraco`
