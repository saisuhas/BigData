README
-------------------------------------
Part 1
-------------------------------------

This is the link for the jar file
https://graphxframes.s3.amazonaws.com/SentimentalAnalysis-assembly-0.1.jar

1. Import the SentimentalAnalysis project to IntelliJ IDEA

2. Open bash and give sbt command.

3. Then, type assembly to build the fat jar. (sbt:SentimentalAnalysis> assembly)

4. Start the zookeeper using the following command
bin/zookeeper-server-start.sh config/zookeeper.properties

5. Start the kafka using the following command
bin/kafka-server-start.sh config/server.properties

6. Create the topic using the command
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic facebook

7. Start the consumer using the command
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic facebook --from-beginning

8. Run the Spark project using the command
spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0
--class KafkaExample PathToJarFile facebook 10 COVID19

(We are passing three arguments here. Topic name i.e., facebook, windowlength i.e., 10, and filter i.e., COVID19.
KafkaExample is the main class name.) 

9. The sentiments starts to appear in consumer console

10. Add the logstash-simple.conf file in the Logstash directory with the following content.

input {
kafka {
bootstrap_servers => "localhost:9092"
topics => ["facebook"]
}
}
output {
elasticsearch {
hosts => ["localhost:9200"]
index => "facebook-index"
}
}

11. Launch Elastic search, kibana and Logstash to view the visualization dashboard.
12. Go to logstash directory in the terminal and enter logstash-simple.conf
13. The visualisation results will appear at http://localhost:5601 


