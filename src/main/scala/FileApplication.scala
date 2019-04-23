import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}


object FileApplication {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    import spark.implicits._


    // Get our enrichment stream (name -> id mapping)
    val mappings = spark.read.option("header", "true").csv("src/main/resources/mapping.csv")


    // lets start with a file based input, keep looking for new files in this directory
    // NOTE - this assumes each record is on an individual line
    val inputFileData = spark.readStream.schema(buildSchema()).json("src/main/resources/readingfrom")

    // check this is streaming
    println("I HAVE A STREAMING DATA SOURCE? " + inputFileData.isStreaming)

    // if file looks promising can move to Kafka later
    // see https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html


    // Enrich data with id
    val mappedData = inputFileData.join(mappings, Seq("name"),"left_outer")

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
