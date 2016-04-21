package com.scala.kafka

import java.util.Date
import java.util.concurrent.atomic.AtomicLong
import com.scala.kafka.entity.Message
import com.scala.kafka.producer.SimpleProducer
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization

/**
 * Yuliia Vovk 
 * 28.12.15
 */
object MainProducer extends App with Config with LazyLogging {

  private val id = new AtomicLong()

  private val thread = new Thread(new Runnable() {
    def run(): Unit = {
      try {
        while (!Thread.currentThread().isInterrupted) {
          SimpleProducer.send(kafkaTopic, convertToJSON(generateMessage()))
          Thread.sleep(500)
        }
      }
      catch {
        case ex: InterruptedException => logger.error("Interrupted exception occurred!")
      }
    }
  })
  thread.start()

  def generateMessage(): Message = {
    id.getAndIncrement()
    new Message(id.get(), "Hello world", new Date().getTime)
  }

  def convertToJSON(message: Message): String = {
    implicit val formats = DefaultFormats
    Serialization.write(message)
  }
}
