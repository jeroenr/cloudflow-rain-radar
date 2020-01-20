package com.github.jeroenr.rain.radar

import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class RainClutterPartitioner extends AkkaStreamlet {
  val in = AvroInlet[PrecipitationData]("in")
  val clutter = AvroOutlet[Clutter]("clutter").withPartitioner(RoundRobinPartitioner)
  val rain = AvroOutlet[Rain]("rain").withPartitioner(_.city)
  val shape = StreamletShape(in).withOutlets(clutter, rain)

  override def createLogic = new SplitterLogic(in, clutter, rain) {
    def flow = flowWithOffsetContext()
      .filter(_.value > 0) // disregard data if it's dry
      .map { precipitationData ⇒
        if(precipitationData.value <= 0.1) Left(Clutter(precipitationData.timestamp, precipitationData.value))
        else Right(Rain(precipitationData.timestamp, precipitationData.location.city, precipitationData.value))
      }
  }
}