#!/bin/sh

SCRIPT_DIR=`realpath $(dirname "$0")`
docker run -it --network=burraco-project -v $SCRIPT_DIR/content:/var/content:ro -v $SCRIPT_DIR/solutions:/var/solutions:ro --rm kafka /bin/bash
