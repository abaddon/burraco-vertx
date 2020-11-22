#!/bin/sh
docker build -t kafka_terminal -f image_kafka_terminal/Dockerfile image_kafka_terminal
docker run -it --network=burraco-project --rm kafka_terminal /bin/bash
