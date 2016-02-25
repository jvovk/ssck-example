package com.scala.kafka.producer

import com.scala.kafka.MainProducer
import MainProducer._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
 * Yuliia Vovk
 * 28.12.15
 */
object SimpleProducer {

  private val producer = new KafkaProducer[AnyRef, String](producerConfig)

  def send(topic: String, mes: String): Unit = {
    producer.send(buildMessage(topic, mes))
  }

  def shutdown(): Unit = if (producer != null) producer.close()

  private def buildMessage(topic: String, mes: String): ProducerRecord[AnyRef, String] = new ProducerRecord(topic, mes)

}
