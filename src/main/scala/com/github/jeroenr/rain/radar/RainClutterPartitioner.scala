package com.github.jeroenr.rain.radar

import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector

class RainClutterPartitioner extends FlinkStreamlet {
  val in = AvroInlet[PrecipitationData]("in")
  val out = AvroOutlet[Rain]("rain").withPartitioner(_.city)
  val clutter = AvroOutlet[Clutter]("clutter").withPartitioner(RoundRobinPartitioner)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic {
    val outputTag = OutputTag[Clutter]("clutter-output")

    override def buildExecutionGraph = {
      val stream = readStream(in)
        .filter(_.value > 0) // disregard data if it's dry
        .process[Rain]((
                   precipitationData: PrecipitationData,
                   ctx: ProcessFunction[PrecipitationData, Rain]#Context,
                   out: Collector[Rain]) => {
          if (precipitationData.value > 0.1)
            out.collect(Rain(precipitationData.timestamp, precipitationData.location.city, precipitationData.value))
          else
            ctx.output(outputTag, Clutter(precipitationData.timestamp, precipitationData.value))
        })
      writeStream(out, stream)
      writeStream(clutter, stream.getSideOutput(outputTag))
    }
  }

  override def shape(): StreamletShape = StreamletShape(in).withOutlets(out, clutter)
}
