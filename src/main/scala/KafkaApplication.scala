import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.functions._


object KafkaApplication {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    import spark.implicits._


    // Get our enrichment stream (name -> id mapping)
    val mappings = spark.read.option("header", "true").csv("src/main/resources/mapping.csv")


    // source data from kafka, assumes broker is running locally on port 9092 and we have a topic
    // called "streaming"
    val inputKafkaStream = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "streaming")
      .load()

    // check this is streaming
    println("I HAVE A STREAMING DATA SOURCE? " + inputKafkaStream.isStreaming)


    val inputData = inputKafkaStream.select(
      col("key").cast("string"),
      from_json(col("value").cast("string"), buildSchema()).alias("parsedJson"))

    val jsonRecord = inputData.select("parsedJson.*")

    // Enrich data with id
    val mappedData = jsonRecord.join(mappings, Seq("name"),"left_outer")

    // print results to console as they come in
    val query = mappedData.writeStream.format("console").start()

    // run until killed
    query.awaitTermination()

  }


  def buildSchema() : StructType = {
    val schema = StructType(
      Array(
        StructField("name", StringType, true),
        StructField("size", StringType, true)
      )
    )
    schema
  }

}
