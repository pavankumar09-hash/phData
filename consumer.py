from kafka import KafkaConsumer
import json
import logging
with open("consumer_config.json", "r") as config:
    data = json.load(config)
consumerconfig=data["ConsumerConfig"]
bannedList=consumerconfig["BannedIPList"]

#subscribe to botnet_topic
consumer = KafkaConsumer('botnet_topic',bootstrap_servers='localhost:9092')
botlog=open("botlog.txt","a")
for msg in consumer:
 strMessage= msg.value.decode('ascii')
 splitMessage=strMessage.split()
 strIP=splitMessage[0]

#Check if the ip is in banned list. Since this is a mock I am pulling it from json otherwise it could
# be coming from another utility or program that identifies suspicious IP based on an algorithm.
 if strIP in bannedList:
   print(strIP)
   botlog.write(strIP+"\n")
botlog.close()  
if consumer is not None:
 consumer.close()
