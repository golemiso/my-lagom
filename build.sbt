
organization in ThisBuild := "com.golemiso"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "5.0.0"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

lazy val `common` = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      playJsonDerivedCodecs
    )
  )

lazy val `player-api` = (project in file("player/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `player-impl` = (project in file("player/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`player-api`)

lazy val `team-api` = (project in file("team/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `team-impl` = (project in file("team/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`team-api`)

lazy val `battle-api` = (project in file("battle/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )
  .dependsOn(`common`)

lazy val `battle-impl` = (project in file("battle/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`battle-api`)

lazy val `competition-api` = (project in file("competition/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `competition-impl` = (project in file("competition/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`competition-api`)

lazy val `aggregation-api` = (project in file("aggregation/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `aggregation-impl` = (project in file("aggregation/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`aggregation-api`, `player-api`, `competition-api`, `team-api`, `battle-api`)

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
  .dependsOn(`aggregation-api`, `player-api`, `competition-api`, `team-api`, `battle-api`)

lagomCassandraCleanOnStart in ThisBuild := true
