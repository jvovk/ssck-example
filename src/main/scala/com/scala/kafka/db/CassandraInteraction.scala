package com.scala.kafka.db

import com.datastax.spark.connector.cql._
import com.scala.kafka.MainStreamer
import com.scala.kafka.entity.FinalNotification
import MainStreamer._
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.spark.rdd._
import com.datastax.spark.connector._

/**
  * Yuliia Vovk
  * 29.12.15
  */
object CassandraInteraction extends LazyLogging {

  def init(): Unit = {
    val replicationFactor = 1
    logger.info("Creating Cassandra keyspace if necessary...")

    CassandraConnector(sparkConfig).withSessionDo { session =>
      session.execute(
        s"""CREATE KEYSPACE IF NOT EXISTS $keyspace
                            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': $replicationFactor}""")

      if (dropTable) {
        logger.info(s"Dropping table $keyspace.notifications...")
        session.execute( s"""DROP TABLE IF EXISTS $keyspace.notifications""")
        session.execute( s"""CREATE TABLE $keyspace.notifications ( id bigint, notification text, device_guid text, timestamp text, mac text, uuid text, value double, PRIMARY KEY (id, timestamp) )""")
      }
    }
  }

  def save(rdd: RDD[FinalNotification]): Unit = {
    rdd.saveToCassandra(keyspace, "notifications")
  }

}
