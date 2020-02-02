##
## Producer script that accepts data log and sends to a topic
## log entries are written into the topic iteratively.
## After the logs are written into the topic a consumer program written in scala
## reads the logs, applies transformations and writes the black listed IPS to 
## logs
##

# import kafkaproducer from kafka module
from kafka import KafkaProducer

# Create a kafka producer by passing the kafka server host and port.
producer = KafkaProducer(bootstrap_servers='localhost:9092')

# Read the data log
log = open("phData.log")

# loop through the log
for logentry in log:

# Write every log entry to the topic. Creating a topic is a manual script
# by calling the kafka shell script bin/kafka-topics.sh --create
 producer.send('botnet_topic', logentry.encode('ascii'))


# All the logentries are written into teh topic. Now Close the log file
log.close()


#Close the producer handle. 
if producer is not None:
 producer.close()
