package com.scala.kafka

import java.io.File
import java.util.Properties

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.JavaConversions._

/**
  * Yuliia Vovk
  * 02.01.16
  */
trait Config extends LazyLogging {
  self: App =>

  lazy val sc = new SparkContext(sparkConfig)

  lazy val conf = args match {
    case Array("--config", extraConfigPath) =>
      logger.info(s"Loading config from '$extraConfigPath'...")
      ConfigFactory
        .parseFile(new File(extraConfigPath))
        .withFallback(ConfigFactory.parseResources("reference.conf"))
        .resolve()
    case unk =>
      logger.info(s"Loading reference config...")
      ConfigFactory
        .parseResources("reference.conf")
        .resolve()
  }

  lazy val sparkConfig = {
    val sparkConfigSelection = conf.getConfig("spark")
    val sparkKeySection = sparkConfigSelection.entrySet().toList.map("spark." + _.getKey)

    sparkKeySection.foldLeft(new SparkConf(true).setAppName(this.getClass.getSimpleName).setMaster("local[*]")) { (cfg, key) =>
      cfg.set(key, conf.getString(key))
    }
  }

  object producer  {
    lazy val servers =  conf.getString("producer.servers")
    lazy val serializer = conf.getString("producer.serializer")
    lazy val numThreads = conf.getInt("producer.numThreads")
  }

  lazy val producerConfig = {
    val props = new Properties()
    props.put("bootstrap.servers", producer.servers)
    props.put("value.serializer", producer.serializer)
    props.put("key.serializer", producer.serializer)
    props
  }

  object zookeeper  {
    lazy val connect =  conf.getString("zookeeper.connect")
    lazy val group = conf.getString("zookeeper.group")
  }

  lazy val zookeeperConfig = {
    val props = new Properties()
    props.put("zookeeper.connect", zookeeper.connect)
    props.put("group.id", zookeeper.group)
    props
  }

  object streamer  {
    lazy val numThreads =  conf.getInt("streamer.num-threads")
    lazy val timeInterval = conf.getInt("streamer.time-interval")
    lazy val timeWindow = conf.getInt("streamer.time-window")
  }

  lazy val kafkaTopic = conf.getString("kafka-topic")
  lazy val keyspace = conf.getString("keyspace")
  lazy val clusterName = conf.getString("cluster-name")
  lazy val table = conf.getString("table")
  lazy val dropTable = conf.getBoolean("drop-table")
  lazy val cassandraEnabled = conf.getBoolean("cassandra-integration")

}
