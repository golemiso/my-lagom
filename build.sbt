
organization in ThisBuild := "com.golemiso"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val playJson = "com.typesafe.play" %% "play-json" % "2.7.1"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

lazy val `my-lagom` = (project in file("."))
  .aggregate(
    `player-api`,
    `player-impl`,
    `web-gateway`
  )

lazy val `common` = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      playJson
    )
  )

lazy val `my-lagom-api` = (project in file("my-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `my-lagom-impl` = (project in file("my-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`my-lagom-api`)

lazy val `my-lagom-stream-api` = (project in file("my-lagom-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `my-lagom-stream-impl` = (project in file("my-lagom-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`my-lagom-stream-api`, `my-lagom-api`)

lazy val `player-api` = (project in file("player-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `player-impl` = (project in file("player-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`player-api`)

lazy val `web-gateway` = (project in file("web-gateway"))
  .enablePlugins(LagomPlayScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      macwire,
      "org.reactivemongo" %% "reactivemongo" % "0.13.0",
      scalaTestPlusPlay
    ),
    javaOptions in Test += "-Dconfig.file=test/resources/test.conf"
  )
  .dependsOn(`common`, `player-api`)
