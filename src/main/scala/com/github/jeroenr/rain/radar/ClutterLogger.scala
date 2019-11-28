package com.github.jeroenr.rain.radar

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class ClutterLogger extends AkkaStreamlet {
  val inlet = AvroInlet[Clutter]("in")
  val shape = StreamletShape.withInlets(inlet)

  override def createLogic = new RunnableGraphStreamletLogic() {
    def flow = {
      FlowWithOffsetContext[Clutter]
        .map { clutter ⇒
          system.log.debug(s"Clutter detected: $clutter")
          clutter
        }
    }

    def runnableGraph = {
      sourceWithOffsetContext(inlet).via(flow).to(sinkWithOffsetContext)
    }
  }
}
