import KApp.{kafkaParams, streamingContext, topic}
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.concurrent.duration._

object App extends App {

  println("Simple Spark Job")

  val conf: SparkConf = new SparkConf()
    .setAppName("Simple Nlp Spark Job")
    .setMaster("local[2]")
    .set("spark.cores.max", "2")
    .set("spark.driver.cores", "2")
    .set("spark.driver.memory", "212M")
    .set("spark.testing.reservedMemory", "37425008")
    .set("spark.executor.memory", "212M")


  val sc: SparkContext = SparkContext.getOrCreate(conf)

  val sqlContext = SQLContext.getOrCreate(sc)


  val interval = 5 seconds
  val streamingContext = new StreamingContext(sc, Seconds(interval.toSeconds))

  val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
    streamingContext, kafkaParams, Set(topic))
    .map {
      case (_, value) =>
        value
    }


  stream.count().print()

  streamingContext.start()

  streamingContext.awaitTermination()

//  import sqlContext.implicits._
//  val input = Seq(
//    (1, "I remember clearly the statement by the then Secretary of State for Transport announcing HS2 for the north. I asked him then whether it would go to Derbyshire Dales and of course the answer was no, but one thing was certain: it was going to the very heavily populated eastern side of Derbyshire. That meant there was going to be some trouble. Sure enough, during the past few months, I have been meeting people and trying to deal with that trouble in Tibshelf and other parts of Bolsover. In an industrial estate in Tibshelf, the line goes straight through the factory owned by a firm employing nearly 100 people.")
//  ).toDF("id", "text")

//  import org.apache.spark.sql.functions._
//  import com.databricks.spark.corenlp.functions._


//  val output = input
//      .select(cleanxml('text).as('doc))
//      .select(explode(ssplit('doc)).as('sen))
//      .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags), sentiment('sen).as('sentiment))
//
//    output.show(truncate = false)


//  val NUM_SAMPLES: Int = 15000
//
//  val join = sc.parallelize(1 to NUM_SAMPLES).filter { _ =>
//    val x = math.random
//    val y = math.random
//    x * x + y * y < 1
//  }.count()
//  println(s"Pi is roughly ${4.0 * join / NUM_SAMPLES}")

  def stream(topicName: String, version: String): DStream[String] = {
    // Change to zookeeper address instead???
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> "localhost:9092",
      "group.id" -> "Spark-Reader",
      "client.id" -> "SR-1",
      "auto.offset.reset" -> "largest"
    )

    val topic = topicName

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      streamingContext, kafkaParams, Set(topic))
      .map {
        case (_, value) =>
          value
      }
  }
}
