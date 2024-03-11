#!/bin/bash

/usr/bin/mongod --config /etc/mongod.conf &

mysqld --user=root --init-file=/root/installation/create-user.sql &

/root/installation/kafka_2.13-3.7.0/bin/zookeeper-server-start.sh /root/installation/kafka_2.13-3.7.0/config/zookeeper.properties &
/root/installation/kafka_2.13-3.7.0/bin/kafka-server-start.sh /root/installation/kafka_2.13-3.7.0/config/server.properties &

source /root/.bashrc

cd node-server
./run.sh &

cd ..
/root/installation/wildfly-preview-26.1.3.Final/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
