import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.conf.ConfigurationBuilder
import twitter4j.auth.OAuthAuthorization
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaExample {
  def main(args: Array[String]) {
    if (args.length<2) {
      System.err.println("Input Arguments <KafkaTopic> Filter")
      System.exit(1)
    }

    val topic = args(0).toString
    val filters = args.slice(2, args.length)
    val winLen=args(1).toInt

    if (!Logger.getRootLogger.getAllAppenders.hasMoreElements) {
      Logger.getRootLogger.setLevel(Level.WARN)
    }

    val sparkConfiguration = new SparkConf().setAppName("TwitterAnalysis")

    if (!sparkConfiguration.contains("spark.master")) {
      sparkConfiguration.setMaster("local[2]")
    }

    val ssc = new StreamingContext(sparkConfiguration, Seconds(winLen))


    val configBuild = new ConfigurationBuilder
    configBuild.setDebugEnabled(true).setOAuthConsumerKey("axPEFuSM7oiN2RmzRvmnBpumc")
      .setOAuthConsumerSecret("A81LMjbmUBsaEWKLaVnBXFIAyPXoIThO69CqzA4qfCWeNpoTlF")
      .setOAuthAccessToken("2160328664-YSSyE1iesMrgoZ7d6uEE3haANv2uamJNIYYekOy")
      .setOAuthAccessTokenSecret("rIfU4DYfRy34M63T3SRL2Hv1JThxf9qNZLGjTwepeulkF")
    val auth = new OAuthAuthorization(configBuild.build)

    val crestr = TwitterUtils.createStream(ssc, Some(auth),filters)
    val engTweets = crestr.filter(_.getLang() == "en")

    val keyWords = engTweets.map(status => status.getText)
      .map(sentence=>(SentimentAnalysis.mainSentiment(sentence)))
    keyWords.saveAsTextFiles("datum/test/tweets", "json")

    keyWords.foreachRDD { (rdd, time) =>

      rdd.foreachPartition { partitionIter =>

        val properties = new Properties()
        val bootstrap = "localhost:9092"
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties.put("bootstrap.servers", bootstrap)
        val producer = new KafkaProducer[String, String](properties)
        partitionIter.foreach { elem =>
          val data = elem.toString()
          val datum = new ProducerRecord[String, String](topic, null, data)
          producer.send(datum)
        }
        producer.flush()
        producer.close()
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}