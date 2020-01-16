import sbt._
import sbt.Keys._

lazy val rainRadar =  (project in file("."))
    .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka"      %% "akka-http-spray-json"      % "10.1.10",
        "ch.qos.logback"         %  "logback-classic"           % "1.2.3"
      ),
      name := "rain-radar",
      organization := "com.github.jeroenr",

      scalaVersion := "2.12.10",
      crossScalaVersions := Vector(scalaVersion.value)
    )
