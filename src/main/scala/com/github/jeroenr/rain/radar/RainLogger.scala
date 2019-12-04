package com.github.jeroenr.rain.radar

import akka.event.Logging
import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import org.apache.avro.specific.SpecificRecordBase

import scala.reflect.ClassTag

abstract class LoggerStreamlet[T <: SpecificRecordBase : ClassTag](template: String,
                                                                   logLevel: Logging.LogLevel = Logging.InfoLevel) extends AkkaStreamlet {
  val inlet = AvroInlet[T](name = "in")
  val shape = StreamletShape.withInlets(inlet)

  override def createLogic = new RunnableGraphStreamletLogic() {
    def flow = {
      FlowWithOffsetContext[T]
        .mapContext { element ⇒
          system.log.log(logLevel, template, element)
          element
        }
    }

    def runnableGraph = {
      sourceWithOffsetContext(inlet).via(flow).to(sinkWithOffsetContext)
    }
  }
}

class RainLogger extends LoggerStreamlet[Rain]("Rain detected: {}")

class ClutterLogger extends LoggerStreamlet[Clutter]("Clutter detected: {}")
