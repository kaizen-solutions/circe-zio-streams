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
    name             := "circe-zio-stream",
    organization     := "io.kaizen-solutions",
    organizationName := "kaizen-solutions"
  )
  .settings(
    libraryDependencies ++= {
      val zio  = "dev.zio"
      val zioV = "2.0.13"
      Seq(
        zio        %% "zio-streams"  % zioV,
        "io.circe" %% "circe-jawn"   % "0.14.5",
        zio        %% "zio-test"     % zioV % Test,
        zio        %% "zio-test-sbt" % zioV % Test
      )
    }
  )
