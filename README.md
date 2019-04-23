
Uses SBT, so simply
  sbt package

Works with Java 10, scala 2.11

To run, easiest to use spark submit

* download spark: https://spark.apache.org/downloads.html
* execute with: spark-submit --class Application --master local[4] target/scala-2.11/streaming101_2.11-0.1.jar

