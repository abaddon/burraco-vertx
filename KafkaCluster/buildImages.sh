#!/bin/sh

docker build -t zookeeper-node -f image_zk_node/Dockerfile image_zk_node
docker build -t kafka-node -f image_kafka_node/Dockerfile image_kafka_node
docker build -t kafka -f image_kafka/Dockerfile image_kafka


# docker build -t kafka-training -f image/Dockerfile image

# docker build -t kafka-training-250 -f image_250/Dockerfile image
