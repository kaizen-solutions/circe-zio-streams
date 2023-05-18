package io.kaizensolutions.zio.streams.circe

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import org.typelevel.jawn.ParseException
import zio.{System => _, _}
import zio.stream._
import zio.test._
import zio.test.magnolia.DeriveGen

object ParserSpec extends ZIOSpecDefault {
  override def spec =
    suite("Parser Specification")(
      suite("Streaming JSON Arrays")(
        test("parses valid streaming bodies with no spaces")(
          testcase(Parser.jsonArrayPipeline)(JsonStream.jsonArrayStream(_.noSpaces))
        ) +
          test("parses valid streaming bodies with spaces and new lines")(
            testcase(Parser.jsonArrayPipeline)(JsonStream.jsonArrayStream(_.spaces2))
          ) +
          test("parses valid streaming bodies with more spaces and new lines")(
            testcase(Parser.jsonArrayPipeline)(JsonStream.jsonArrayStream(_.spaces4))
          )
      ) +
        suite("Streaming JSON bodies separated by new lines")(
          test("parses valid streaming bodies with no spaces in JSON")(
            testcase(Parser.jsonStreamPipeline)(JsonStream.valueStream(_.noSpaces))
          ) +
            test("parses valid streaming bodies with spaces and new lines")(
              testcase(Parser.jsonStreamPipeline)(JsonStream.valueStream(_.spaces2))
            ) +
            test("parses valid streaming bodies with more spaces and new lines")(
              testcase(Parser.jsonStreamPipeline)(JsonStream.valueStream(_.spaces4))
            )
        )
    )

  private def testcase(
    pipeline: ZPipeline[Any, ParseException, Byte, Json]
  )(fn: UStream[Example] => ZStream[Any, Throwable, Byte]): Task[TestResult] =
    check(Example.genStream) { examples =>
      val expected = examples.runCollect
      val actual =
        fn(examples)
          .via(pipeline)
          .mapChunks(_.map(_.as[Example]).collect { case Right(value) => value })
          .runCollect

      for {
        actual   <- actual
        expected <- expected
      } yield assertTrue(actual == expected)
    }

}

final case class Example(a: Int, b: String, c: Boolean)
object Example {
  implicit val exampleCodec: Codec[Example] = deriveCodec[Example]

  val gen: Gen[Any, Example] = DeriveGen[Example]

  val genStream: Gen[Any, UStream[Example]] =
    Gen.chunkOf1(gen).map(ZStream.fromChunk(_))
}

object JsonStream {
  def valueStream[A: Encoder](stringifyJson: Json => String)(stream: UStream[A]): ZStream[Any, Throwable, Byte] =
    stream
      .mapChunks(_.map(e => stringifyJson(e.asJson)))
      .intersperse(System.lineSeparator())
      .via(ZPipeline.utf8Encode)

  def jsonArrayStream[A: Encoder](stringifyJson: Json => String)(stream: UStream[A]): ZStream[Any, Throwable, Byte] =
    (
      ZStream("[") ++
        stream.map(e => stringifyJson(e.asJson)).intersperse(",") ++
        ZStream("]")
    ).via(ZPipeline.utf8Encode)
}
