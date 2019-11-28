package com.github.jeroenr.rain.radar

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class RainLogger extends AkkaStreamlet {
  val inlet = AvroInlet[Rain]("in")
  val shape = StreamletShape.withInlets(inlet)

  override def createLogic = new RunnableGraphStreamletLogic() {
    def flow = {
      FlowWithOffsetContext[Rain]
        .map { rain ⇒
          system.log.info(s"Rain detected: $rain")
          rain
        }
    }

    def runnableGraph = {
      sourceWithOffsetContext(inlet).via(flow).to(sinkWithOffsetContext)
    }
  }
}
