import sbtrelease.ReleaseStateTransformations._

inThisBuild {
  val scala212 = "2.12.17"
  val scala213 = "2.13.10"
  val scala32  = "3.2.2"

  Seq(
    scalaVersion                        := scala32,
    crossScalaVersions                  := Seq(scala212, scala213, scala32),
    githubWorkflowPublishTargetBranches := Seq.empty,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    releaseTagName := s"${version.value}"
  )
}

lazy val root = (project in file("."))
  .settings(
    name             := "circe-zio-streams",
    organization     := "io.kaizen-solutions",
    organizationName := "kaizen-solutions"
  )
  .settings(
    libraryDependencies ++= {
      val circe  = "io.circe"
      val circeV = "0.14.5"

      val zio  = "dev.zio"
      val zioV = "2.0.13"
      Seq(
        zio   %% "zio-streams"       % zioV,
        circe %% "circe-jawn"        % circeV,
        circe %% "circe-generic"     % circeV % Test,
        zio   %% "zio-test"          % zioV   % Test,
        zio   %% "zio-test-magnolia" % zioV   % Test,
        zio   %% "zio-test-sbt"      % zioV   % Test
      )
    }
  )
  .settings(
    versionScheme               := Some("early-semver"),
    releaseIgnoreUntrackedFiles := true,
    releaseTagName              := s"${version.value}",
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    publishTo                   := None,
    publish                     := (()),
    releaseIgnoreUntrackedFiles := true
  )
