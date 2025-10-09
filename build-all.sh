#!/bin/bash
# Save this as: build-all.sh

# Enable BuildKit for better caching
export DOCKER_BUILDKIT=1

echo "Building base image with all modules..."
docker build -f DockerFile-base -t base-builder .

echo "Building Game module..."
cd Game
docker build -t game-service .
cd ..

echo "Building Dealer module..."
cd Dealer
docker build -t dealer-service .
cd ..

echo "Building Player module..."
cd Player
docker build -t player-service .
cd ..

echo "All modules built successfully!"