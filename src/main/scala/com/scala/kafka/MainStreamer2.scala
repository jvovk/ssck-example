package com.scala.kafka

import com.scala.kafka.db.CassandraInteraction
import com.scala.kafka.entity._
import kafka.serializer.StringDecoder
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
  * Created by YV on 28.01.16.
  */
object MainStreamer2 extends App with Config {

  CassandraInteraction.init()

  val ssc = new StreamingContext(sc, Seconds(3))

  val messagesStream = KafkaUtils
    .createStream[String, String, StringDecoder, StringDecoder](ssc,
    Map("zookeeper.connect" -> zookeeper.connect, "group.id" -> "group.id"),
    Map("device_notification" -> streamer.numThreads), StorageLevel.MEMORY_ONLY)
    .map { case (k, v) =>
      implicit val formats = DefaultFormats
      val n = parse(v).extract[Notification]

      def convertJson(json: Option[String]): Parameters = json match {
        case None => throw new IllegalArgumentException("Json can't be converted. ")
        case Some(j) => parse(j).extract[Parameters]
      }

      val param = convertJson(n.parameters.get("jsonString"))
      FinalNotification(n.id, n.notification, n.deviceGuid, n.timestamp, param.mac, param.uuid, param.value)
    }

  messagesStream
    .foreachRDD(rdd => CassandraInteraction.save(rdd))

  ssc.start()

}
