import findspark
findspark.init('/home/ithapu_pavankumar/spark/spark-2.4.4-bin-hadoop2.7/')
from  pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils
#Create a streaming context with 2 worker threads
sc = SparkContext("local[4]",appName="DDOSAttackDetector")
#sc.setLogLevel("WARN")
ssc = StreamingContext(sc, 1)

botnetstream=KafkaUtils.createDirectStream(ssc,["botnet_topic"],{"bootstrap_servers":"localhost:9092"})

