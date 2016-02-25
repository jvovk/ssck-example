package com.scala.kafka.entity

/**
  * Author: Yuliia Vovk
   Date: 25.02.16
   Time: 10:32
  */
case class Parameters(mac: String, uuid: String, value: Double)

case class Notification(id: Int, notification: String, deviceGuid: String, timestamp: String, parameters: Map[String, String])

case class FinalNotification(id: Int, notification: String, device_guid: String, timestamp: String, mac: String, uuid: String, value: Double)
