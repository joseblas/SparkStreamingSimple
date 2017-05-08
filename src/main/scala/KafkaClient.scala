import KApp.props
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by jta on 05/05/2017.
  */
object KafkaClient {
  val producer = new KafkaProducer[String, String](props)

  def send(msg: String) = {
    val message = new ProducerRecord[String, String]("test_back", "", msg)
    producer.send(message)
  }
}
