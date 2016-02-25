package com.scala.kafka

import com.scala.kafka.consumer.SimpleConsumer
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
  * Yuliia Vovk
  * 30.12.15
  */
object MainConsumer extends App with Config with LazyLogging {

  SimpleConsumer.receiveMessages()

}
