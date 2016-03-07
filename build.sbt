version := "1.0"

name := "julia-vovk-s-practice-kafka-project"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.apache.spark" %% "spark-core" % "1.4.1" % "compile",
  "org.apache.spark" %% "spark-sql" % "1.4.1" % "provided",
  "org.scala-lang" % "scala-library" % "2.10.3",
  "org.apache.spark" %% "spark-hive" % "1.4.1" exclude("org.apache.spark", "spark-core_2.10"),
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.4.1",
  "org.apache.spark" % "spark-streaming_2.10" % "1.4.1",
  "org.apache.kafka" % "kafka_2.10" % "0.8.2.1"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("org.slf4j", "slf4j-simple"),
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.4.1" exclude("org.apache.spark", "spark-core_2.10"),
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.6.2",
  "com.rockymadden.stringmetric" %% "stringmetric-core" % "0.27.3")

resolvers += Resolver.mavenLocal

assemblyJarName in assembly := "kafka-spark-cassandra-example.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x if Assembly.isConfigFile(x) => MergeStrategy.concat
  case x => MergeStrategy.first
}

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}