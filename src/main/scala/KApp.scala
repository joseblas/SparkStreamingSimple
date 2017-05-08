
import java.util.Properties

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.collection.mutable

/**
  * Created by jta on 05/05/2017.
  */
object KApp extends App {

  val brokers = "localhost:9092"
  val kafkaParams = Map[String, String](
    "metadata.broker.list" -> brokers,
    "group.id" -> "Spark-Reader",
    "client.id" -> "SR-1",
    "auto.offset.reset" -> "largest"
  )

  val topic = "test"

  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


  println("Simple Spark Streaming Job")

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

  import scala.concurrent.duration._

  val interval = 5 seconds
  val streamingContext = new StreamingContext(sc, Seconds(interval.toSeconds))


  val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
    streamingContext, kafkaParams, Set(topic))
    .map {
      case (_, value) =>
        value
    }

  import sqlContext.implicits._
  import org.apache.spark.sql.functions._
  import com.databricks.spark.corenlp.functions._

  stream.filter(!_.isEmpty).foreachRDD { rdd =>

    val df = rdd.toDF("text")
    val output = df
      //      .select(cleanxml('text).as('doc))
      .select(explode(ssplit('text)).as('sen))
      .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags))
    // , sentiment('sen).as('sentiment)

    output.show(truncate = false)


    output.rdd.foreach {
      r =>
        println(r)
        val msg = r.mkString(" ")
        println(s"msg: ${msg}")
        KafkaClient.send(msg)

    }
  }


  //  val input = Seq(
  //    (1, "I remember clearly the statement by the then Secretary of State for Transport announcing HS2 for the north. I asked him then whether it would go to Derbyshire Dales and of course the answer was no, but one thing was certain: it was going to the very heavily populated eastern side of Derbyshire. That meant there was going to be some trouble. Sure enough, during the past few months, I have been meeting people and trying to deal with that trouble in Tibshelf and other parts of Bolsover. In an industrial estate in Tibshelf, the line goes straight through the factory owned by a firm employing nearly 100 people.")
  //  ).toDF("id", "text")


  streamingContext.start()

  streamingContext.awaitTermination()

}
