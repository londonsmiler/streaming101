
Uses SBT, so simply
  sbt package

Works with Java 10, scala 2.11

To run, easiest to use spark submit

* download spark: https://spark.apache.org/downloads.html

File based spike:

- execute with: spark-submit --class FileApplication --master local[4] target/scala-2.11/streaming101_2.11-0.1.jar
- put json files in main/resources/readingfrom (see test1.json for example of format)
- data will be mapped and output to console

