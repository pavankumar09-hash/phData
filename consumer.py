from kafka import KafkaConsumer
import json
import logging
with open("consumer_config.json", "r") as config:
    data = json.load(config)
consumerconfig=data["ConsumerConfig"]
bannedList=consumerconfig["BannedIPList"]
consumer = KafkaConsumer('botnet_topic',bootstrap_servers='localhost:9092')
#botlog=open("botlog.txt","a")
logging.basicConfig(filename='bot.log', filemode='w')
for msg in consumer:
 strMessage= msg.value.decode('ascii')
 splitMessage=strMessage.split()
 strIP=splitMessage[0]
 if strIP in bannedList:
   print(strIP)
   #botlog.write(strIP+"\n")
   logging.info(strIP)
botlog.close()  
if consumer is not None:
 consumer.close()
