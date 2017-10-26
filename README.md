# cloud-iot-analytics-engine


## Build
```
mvn clean

mvn package

mvn install
```

You should have the following the logs as below after running the commands above.
```
[INFO] --- maven-install-plugin:2.4:install (default-install) @ SparkEngineCore ---
[INFO] Installing /Users/xiningwang/localgit/another-rule-based-analytics-on-spark/target/SparkEngineCore-1.0.jar to /Users/xiningwang/.m2/repository/cloud/iot/analytics/engine/SparkEngineCore/1.0/SparkEngineCore-1.0.jar
[INFO] Installing /Users/xiningwang/localgit/another-rule-based-analytics-on-spark/pom.xml to /Users/xiningwang/.m2/repository/cloud/iot/analytics/engine/SparkEngineCore/1.0/SparkEngineCore-1.0.pom
[INFO] Installing /Users/xiningwang/localgit/another-rule-based-analytics-on-spark/target/SparkEngineCore-1.0-jar-with-dependencies.jar to /Users/xiningwang/.m2/repository/cloud/iot/analytics/engine/SparkEngineCore/1.0/SparkEngineCore-1.0-jar-with-dependencies.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 30.454 s
[INFO] Finished at: 2017-10-25T15:56:05+08:00
[INFO] Final Memory: 77M/1436M
[INFO] ------------------------------------------------------------------------
```

## Environment
Make sure that Kafka is running, for example, we use the docker container to run the Kafka as below.
```
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                                            NAMES
a6975c6bf0c2        spotify/kafka       "supervisord -n"    3 months ago        Up 24 hours         0.0.0.0:2181->2181/tcp, 0.0.0.0:9092->9092/tcp   keen_jones
```

## Watch the Kafka messages
```
docker exec -i -t {kafka-container-instance} bash

cd /opt/kafka_2.11-0.10.1.0/bin

./kafka-console-consumer.sh --zookeeper localhost:2181  --topic rule.output

```

## Start Device Simulator

Use the following command to simulate the device to send the event.
```
java -cp /Users/xiningwang/localgit/another-rule-based-analytics-on-spark/target/SparkEngineCore-1.0-jar-with-dependencies.jar cloud.iot.ai.analytics.KafkaDataProducer
```