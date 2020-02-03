
/*
** This is a consumer program that reads logs from the topic every 2 seconds. A new dstream containing IPs is formed.
** Count of IPS is determined based on the window size and a sliding interval. This new dstream is sorted in descending and the first record in  it 
** is a most likely a bot. This address is logged into an output file.
** Dtd: 2 Feb 2020
** Author: Pavan Kumar Ithapu
** Version: 1.1
*/
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.{PreferBrokers,PreferConsistent}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._ 
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Consumer {
  def main(args: Array[String]) {

   //Create a spark context and streaming context
   val conf = new SparkConf().setMaster("local[2]").setAppName("botnetconsumer")
   val ssc = new StreamingContext(conf, Seconds(2))
   
   //Create loglevel to error to avoid flood of info and warning messages.
   ssc.sparkContext.setLogLevel("ERROR")
   val kafkaParams = Map[String, Object](
     "bootstrap.servers" -> "localhost:9092",
     "key.deserializer" -> classOf[StringDeserializer],
     "value.deserializer" -> classOf[StringDeserializer],
     "auto.offset.reset"-> "earliest",
     "group.id" ->"g123"
   )

   //Provide topic of interest. 
   val topics = Array( "botnet_topic")
   
   //Define a threshold value to blacklist and log an IP
   val thresholdVal=30

   /*
   **  Create a direct stream by providing the spark streaming context. Stream will subscribe to the topic botnet_topic
   **  and connects to the kafka broker localhost:9092
   */
   val stream = KafkaUtils.createDirectStream[String, String](
     ssc,
     PreferConsistent,
     Subscribe[String, String](topics, kafkaParams)
   )

   //Get the value part of the StringDeserializer. It contains the log text.
   val  records=stream.map(_.value)
  
   //Split the records based on the first space. Any record before the first space is an ip in the log record
    val iprecords=records.map(record=>record.split(" ")(0))
  
   /*
   ** Form key,value pairs with value=1. Use reduceByKeyAndwindow
   **  to count the IPS within the specified window 2 minutes and slide interval 10 seconds
   */
   val pairrecords=iprecords.map(recordcount => (recordcount,1)).reduceByKeyAndWindow((val1:Int,val2:Int) => (val1+val2),Seconds(120),Seconds(10))
  
   //Sort the results
   val sortedrecords=pairrecords.transform(recordrdd=>recordrdd.sortBy(_._2,false))
   
   //Blacklisted ips are the ips for which ip count is greater than a predetermined threshold value
   val blackListIPs=sortedrecords.map(record=>if(record._2>thresholdVal) record)
   
   //I am printing it on the screen for demonstration purpose.
   blackListIPs.print()

   //Save the log files into a folder. Here I created a folder log. The prefix and suffix for
   //the log files are First and Last
   blackListIPs.saveAsTextFiles("logs/First","Last")

  //Start the streaming process
   ssc.start

   //Wait for a termination interupt Ctrl+c
   ssc.awaitTermination()
  }
}
