package com.scala.kafka

import org.apache.spark.sql.cassandra.CassandraSQLContext

/**
  * Yuliia Vovk
  * 28.12.15
  */
object MainSQLOperation extends Config with App {

  val csc = new CassandraSQLContext(sc)

  csc.sql(s"""SELECT * from $keyspace.${table} order by id""").show

}
