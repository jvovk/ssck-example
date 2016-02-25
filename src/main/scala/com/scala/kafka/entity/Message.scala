package com.scala.kafka.entity

import com.datastax.spark.connector.cql.{RegularColumn, ColumnDef}
import com.datastax.spark.connector.types.{BigIntType, VarCharType}

/**
  * Yuliia Vovk
  * 03.01.16
  */
case class Message(id: Long, message: String, timestamp: Long)
