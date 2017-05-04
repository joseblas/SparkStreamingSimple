import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object App extends App {

  println("Simple S")

  val conf: SparkConf = new SparkConf()
    .setAppName("Simple S")
    .setMaster("local[1]")
    .set("spark.cores.max", "2")
    .set("spark.driver.cores", "2")
    .set("spark.driver.memory", "212M")
    .set("spark.testing.reservedMemory", "37425008")
    .set("spark.executor.memory", "212M")


  val sc: SparkContext = SparkContext.getOrCreate(conf)

  val sqlContext = SQLContext.getOrCreate(sc)

  import sqlContext.implicits._

  val input = Seq(
    (1, "<xml>Stanford University is located in California. It is a great university.</xml>")
  ).toDF("id", "text")

  import org.apache.spark.sql.functions._
  import com.databricks.spark.corenlp.functions._


  val output = input
      .select(cleanxml('text).as('doc))
      .select(explode(ssplit('doc)).as('sen))
      .select('sen, tokenize('sen).as('words), ner('sen).as('nerTags), sentiment('sen).as('sentiment))

    output.show(truncate = false)


  val NUM_SAMPLES: Int = 15000

  val join = sc.parallelize(1 to NUM_SAMPLES).filter { _ =>
    val x = math.random
    val y = math.random
    x * x + y * y < 1
  }.count()
  println(s"Pi is roughly ${4.0 * join / NUM_SAMPLES}")

}
