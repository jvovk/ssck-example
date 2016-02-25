package com.scala.kafka

import com.scala.kafka.entity.Message
import com.typesafe.scalalogging.slf4j.LazyLogging
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

/**
  * Yuliia Vovk
  * 28.12.15
  */
object MainStreamer extends App with Config with LazyLogging {

  val ssc = new StreamingContext(sc, Seconds(streamer.timeInterval))
  val sqlContext = new SQLContext(sc)

  val messages = KafkaUtils.createStream[String, String, StringDecoder,
    StringDecoder](ssc, Map("zookeeper.connect" -> zookeeper.connect, "group.id" -> zookeeper.group),
    Map(kafkaTopic -> streamer.numThreads), StorageLevel.MEMORY_ONLY)
    .map { case (k, v) => implicit val formats = DefaultFormats; JsonMethods.parse(v).extract[Message] }
    .filter(_.id % 2 == 0)

//   KafkaUtils.createDirectStream(ssc, Map[String, String]("bootstrap.servers" -> producer.servers),
//    Set(kafkaTopic))
//    .map { case (k, v) => implicit val formats = DefaultFormats; JsonMethods.parse(v).extract[Message] }
//    .filter(_.id % 2 == 0)

//  val mes = messages.window(Seconds(streamer.timeWindow))

  import sqlContext.implicits._

  messages
    .map(m => Message(m.id, m.message, m.timestamp))
    .foreachRDD { rdd =>
      rdd.toDF().registerTempTable("messages")
    }

  ssc.start()

}
