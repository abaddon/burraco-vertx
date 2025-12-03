#!/bin/bash
# Phase 3 Optimization: Initialize Kafka topics with multiple partitions for horizontal scaling

set -e

echo "Waiting for Redpanda to be ready..."
sleep 10

echo "Creating Kafka topics with 3 partitions each..."

# Create game topic with 3 partitions
rpk topic create game \
  --brokers redpanda:9092 \
  --partitions 3 \
  --replicas 1 || echo "Topic 'game' already exists"

# Create dealer topic with 3 partitions
rpk topic create dealer \
  --brokers redpanda:9092 \
  --partitions 3 \
  --replicas 1 || echo "Topic 'dealer' already exists"

# Create player topic with 3 partitions
rpk topic create player \
  --brokers redpanda:9092 \
  --partitions 3 \
  --replicas 1 || echo "Topic 'player' already exists"

echo "Kafka topics created successfully:"
rpk topic list --brokers redpanda:9092

echo "Topic details:"
rpk topic describe game --brokers redpanda:9092
rpk topic describe dealer --brokers redpanda:9092
rpk topic describe player --brokers redpanda:9092
