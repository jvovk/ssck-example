package com.scala.kafka.consumer

import java.util.concurrent.{ExecutorService, Executors}
import com.scala.kafka.Config.producer
import com.scala.kafka.Config.producer
import com.scala.kafka.MainConsumer
import com.typesafe.scalalogging.slf4j.LazyLogging
import MainConsumer._
import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}

/**
 * Yuliia Vovk 
 * 29.12.15
 */
object SimpleConsumer {

  private val consumer = Consumer.create(new ConsumerConfig(zookeeperConfig))
  private var executor: ExecutorService = null

  def shutdown(): Unit = {
    if (consumer != null) consumer.shutdown()
    if (executor != null) executor.shutdown()
  }

  def receiveMessages() = {
    val topicCountMap = Map(kafkaTopic -> producer.numThreads)
    val consumerMap = consumer.createMessageStreams(topicCountMap)
    val streams = consumerMap.get(kafkaTopic).get

    executor = Executors.newFixedThreadPool(producer.numThreads)

    for (stream <- streams) {
      executor.submit(new ConsumerHelper(stream))
    }
  }
}

private class ConsumerHelper(stream: KafkaStream[Array[Byte], Array[Byte]]) extends Runnable with LazyLogging {

  def run(): Unit = {
    val it = stream.iterator()

    while (it.hasNext()) {
      val jsonMessage = new String(it.next().message())
      logger.info(s"Message retrieved in consumer: $jsonMessage")
    }
  }
}
