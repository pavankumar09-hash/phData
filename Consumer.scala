//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.spark.streaming.kafka010._
//import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
//import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark._
import org.apache.spark.streaming._
//import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3

//Create a spark context and streaming context
object Consumer {
  def main(args: Array[String]) {
   val conf = new SparkConf().setMaster("local[4]").setAppName("botnetconsumer")
   val ssc = new StreamingContext(conf, Seconds(1))

   val kafkaParams = Map[String, Object](
     "bootstrap.servers" -> "localhost:9092"
   )

   val topics = Array( "botnet_topic")
   val stream = KafkaUtils.createDirectStream[String, String](
     ssc,
     Subscribe[String, String](topics, kafkaParams)
   )
  }
}
