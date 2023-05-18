# Circe ZIO Streams
This project integrates the streaming JSON parsing capabilities of the [Jawn](https://github.com/typelevel/jawn) parser 
with the [Circe library](https://github.com/circe/circe/tree/series/0.14.x/modules/jawn) and the 
[ZIO Streams library](https://zio.dev/docs/datatypes/datatypes_stream) allowing for the parsing of JSON streams.

[![Latest Version](https://jitpack.io/v/kaizen-solutions/circe-zio-streams.svg)](https://jitpack.io/#kaizen-solutions/circe-zio-streams)

```sbt
libraryDependencies += "com.github.kaizen-solutions.circe-zio-streams" %% "circe-zio-streams" % "Tag"
```

## Examples
See the [examples](src/test/scala/io/kaizensolutions/zio/streams/circe/examples/Examples.scala) directory for examples 
of how to use this library.

### Recommended Usage
 * Use the `jsonStreamPipeline` to parse newline separated JSON values
 * Use the `jsonArrayStreamPipeline` to parse JSON arrays

## Notes
Please ensure you select the right parser based on your JSON content. For example, do not use the `jsonStreamPipeline` if
your content is a JSON array. The `jsonStreamPipeline` is for newline separated JSON values. Similarly, do not use the
`jsonArrayStreamPipeline` if your content is newline separated JSON values.

## Credits

This project was heavily inspired by the [circe-fs2](https://github.com/circe/circe-fs2) library. 
I wanted to be able to leverage the streaming capabilities of the Jawn parser with the ZIO Streams library directly.