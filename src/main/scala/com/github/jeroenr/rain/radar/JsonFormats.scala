package com.github.jeroenr.rain.radar

import java.time.Instant

import spray.json._

trait InstantJsonSupport extends DefaultJsonProtocol {
  implicit object InstantFormat extends JsonFormat[Instant] {
    def write(instant: Instant) = JsNumber(instant.toEpochMilli)

    def read(json: JsValue): Instant = json match {
      case JsNumber(value) ⇒ Instant.ofEpochMilli(value.toLong)
      case other ⇒ deserializationError(s"Expected Instant as JsNumber, but got: $other")
    }
  }
}
