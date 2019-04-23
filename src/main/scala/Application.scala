import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}


object Application {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    import spark.implicits._


    // Get our enrichment stream (e.g. sedol -> dpfm id mapping)
    val mappings = spark.read.option("header", "true").csv("src/main/resources/mapping.csv")

    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    // lets start with a file based input,
    // NOTE - this assumes each record is on an individual line
    val inputFileData = spark.readStream.schema(buildSchema()).json("src/main/resources/readingfrom")

    // see what schema we got
    inputFileData.printSchema()

    // check this is streaming
    println("I HAVE A STRAMING DATA SOURCE? " + inputFileData.isStreaming)

    // if file looks promising can move to Kafka later
    // see https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html

    // print stream to console
//    var thing = inputFileData.writeStream.format("console").start()
//    thing.processAllAvailable()


    val mappedData = inputFileData.join(mappings, "name")


    val query = mappedData.writeStream.format("console").start()



/*
    // Split the lines into words
    val words = lines.as[String].flatMap(_.split(" "))

    // Generate running word count
    val wordCounts = words.groupBy("value").count()

    // Start running the query that prints the running counts to the console
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()
*/
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
