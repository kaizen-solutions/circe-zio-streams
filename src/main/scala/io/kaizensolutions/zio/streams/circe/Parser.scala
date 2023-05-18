package io.kaizensolutions.zio.streams.circe

import io.circe.Json
import io.circe.jawn.CirceSupportParser
import org.typelevel.jawn.{AsyncParser, ParseException}
import zio.stream.{ZChannel, ZPipeline}
import zio.{Chunk, ZIO}

object Parser {
  private val supportParser: CirceSupportParser = new CirceSupportParser(maxValueSize = None, allowDuplicateKeys = true)

  private def go(parser: AsyncParser[Json]): ZChannel[Any, ParseException, Chunk[Byte], Any, ParseException, Chunk[Json], Any] =
    ZChannel.readWith[Any, ParseException, Chunk[Byte], Any, ParseException, Chunk[Json], Any](
      chunkByte =>
        parseWith(parser)(chunkByte) match {
          case Left(error) =>
            ZChannel.fail(error)

          case Right(jsonChunk) =>
            ZChannel.write(jsonChunk) *> go(parser)
        },
      error => ZChannel.fail(error),
      done => ZChannel.succeed(done)
    )

  private def parseWith(parser: AsyncParser[Json])(in: Chunk[Byte]): Either[ParseException, Chunk[Json]] =
    parser.absorb(in.toArray)(supportParser.facade).map(Chunk.fromIterable(_))

  private def configuredPipeline(mode: AsyncParser.Mode): ZPipeline[Any, ParseException, Byte, Json] =
    ZChannel
      .fromZIO(ZIO.succeed(supportParser.async(mode)))
      .flatMap(go)
      .toPipeline

  /**
   * Use this pipeline when you have an array of JSON values (normal JSON rules)
   *
   * @return
   */
  def jsonArrayPipeline: ZPipeline[Any, ParseException, Byte, Json] =
    configuredPipeline(AsyncParser.UnwrapArray)

  /**
   * Use this pipeline when you have a stream of JSON values separated by new
   * lines
   *
   * @return
   */
  def jsonStreamPipeline: ZPipeline[Any, ParseException, Byte, Json] =
    configuredPipeline(AsyncParser.ValueStream)
}
