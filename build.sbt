
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "akka-demo",
//    Compile / run / javaOptions ++= Seq(
//      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
//      "--illegal-access=permit"
//    )
  )

libraryDependencies ++= Seq(
  // Logback
//  "ch.qos.logback" % "logback-classic" % "1.5.2",
//  // MongoDB Scala Driver
//  "org.mongodb.scala" %% "mongo-scala-driver" % "4.11.1",
  // Spark
  "org.apache.spark" %% "spark-core" % "3.2.2",
  "org.apache.spark" %% "spark-sql" % "3.2.2",
//  "org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1",
//  "org.mongodb.scala" %% "mongo-scala-driver" % "4.11.1", // MongoDB Scala 드라이버 (버전은 확인 필요)
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.2.1",
  // Akka
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
)
