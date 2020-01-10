#import kafkaproducer from kafka module
from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers='localhost:9092')
log = open("phData.log")
for logentry in log:
 producer.send('botnet_topic', logentry.encode('ascii'))
log.close()
if producer is not None:
 producer.close()
