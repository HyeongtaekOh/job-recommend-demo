ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "akka-demo"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.4",
  "com.typesafe.akka" %% "akka-stream" % "2.6.15",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.15",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4",
  "ch.qos.logback" % "logback-classic" % "1.2.3" // 로깅을 위한 의존성
)
