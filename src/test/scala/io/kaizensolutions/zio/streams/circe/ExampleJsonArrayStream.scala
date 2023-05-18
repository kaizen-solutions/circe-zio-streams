package io.kaizensolutions.zio.streams.circe

import zio.stream._
import zio.{System => _, _}

object ExampleJsonArrayStream extends ZIOAppDefault {
  val jsonArrayStream: UStream[String] = {
    val begin = ZStream("[")
    val end = ZStream("]")
    val json =
      (ZStream("""{"foo": "bar"}""") ++ ZStream(",") ++ ZStream("""{"bar": "baz"}""") ++ ZStream(","))
        .repeat(Schedule.recurs(1000)) ++ ZStream("""{"baz": "qux"}""")
    begin ++ json ++ end
  }

  override val run =
    jsonArrayStream
      .throttleShape(10, 1.second)(_.length)
      .via(ZPipeline.utf8Encode)
      .via(Parser.jsonArrayPipeline)
      .map(_.spaces2)
      .debug("emit>")
      .runDrain
}

object ExampleJsonValuesStream extends ZIOAppDefault {
  val jsonStream: UStream[String] =
    ZStream("""{"foo": "bar"}""", System.lineSeparator(), """{"bar": "baz"}""")
      .repeat(Schedule.recurs(1000))

  override val run =
    jsonStream
      .throttleShape(10, 1.second)(_.length)
      .via(ZPipeline.utf8Encode)
      .via(Parser.jsonStreamPipeline)
      .map(_.spaces2)
      .debug("emit>")
      .runDrain
}
