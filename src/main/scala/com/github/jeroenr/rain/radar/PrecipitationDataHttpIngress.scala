package com.github.jeroenr.rain.radar

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import com.github.jeroenr.rain.radar.PrecipitationDataJsonSupport._

class PrecipitationDataHttpIngress extends AkkaServerStreamlet {
  val out = AvroOutlet[PrecipitationData]("out").withPartitioner(_.location.city)
  def shape = StreamletShape.withOutlets(out)
  override def createLogic = HttpServerLogic.default(this, out)
}