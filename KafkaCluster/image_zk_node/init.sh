#!/bin/bash
echo $MYID > /tmp/zookeeper/myid
/var/kafka/bin/zookeeper-server-start.sh /var/content/zookeeper.properties