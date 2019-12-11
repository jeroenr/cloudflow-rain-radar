import sbt._
import sbt.Keys._

lazy val rainRadar =  (project in file("."))
    .enablePlugins(CloudflowAkkaStreamsApplicationPlugin, CloudflowFlinkApplicationPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka"      %% "akka-http-spray-json"      % "10.1.10",
        "ch.qos.logback"         %  "logback-classic"           % "1.2.3"
      ),
      name := "rain-radar",
      organization := "com.github.jeroenr",

      scalaVersion := "2.12.10",
      crossScalaVersions := Vector(scalaVersion.value),
      scalacOptions ++= Seq(
        "-encoding", "UTF-8",
        "-target:jvm-1.8",
        "-Xlog-reflective-calls",
        "-Xlint",
        "-Ywarn-unused",
        "-Ywarn-unused-import",
        "-deprecation",
        "-feature",
        "-language:_",
        "-unchecked"
      ),
      runLocalConfigFile := Some("resources/local.conf"),

      scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
      scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    )
