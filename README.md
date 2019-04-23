## Intro

Uses SBT, so simply
  sbt package

Works with Java 10, scala 2.11

To run, easiest to use spark submit

* download spark: https://spark.apache.org/downloads.html

## File based spike:

- execute with: spark-submit --class FileApplication --master local[4] target/scala-2.11/streaming101_2.11-0.1.jar
- put json files in main/resources/readingfrom (see test1.json for example of format)
- data will be mapped and output to console

## Kafka based spike:

* download kafka and run as per: https://kafka.apache.org/quickstart
* create a topic called "streaming": bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streaming
* Run spark with spark-submit --class KafkaApplication --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.1 --master local[4] target/sca-2.11/streaming101_2.11-0.1.jar
* publish to kafka with:
bin/kafka-console-producer.sh --broker localhost:9092 --topic streaming
* data will be mapped and output to the console

