organization in ThisBuild := "com.golemiso"
version in ThisBuild := "1.0-SNAPSHOT"
name in ThisBuild := "ama-api"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

val playJson = "com.typesafe.play" %% "play-json" % "2.7.2"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
val lagomScaladslAkkaDiscovery = "com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % com.lightbend.lagom.core.LagomVersion.current

lazy val `common` = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      playJson
    )
  )

lazy val `player-impl` = (project in file("player/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslAkkaDiscovery,
      macwire,
      scalaTest
    )
  )
  .settings(
    packageName in Docker := "ama-player-api"
  )
  .dependsOn(`player-api`)

lazy val `player-api` = (project in file("player/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `battle-impl` = (project in file("battle/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslAkkaDiscovery,
      lagomScaladslKafkaBroker,
      macwire,
      scalaTest
    )
  )
  .settings(
    packageName in Docker := "ama-battle-api"
  )
  .dependsOn(`battle-api`)

lazy val `battle-api` = (project in file("battle/api"))
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
      lagomScaladslAkkaDiscovery,
      macwire,
      scalaTest
    )
  )
  .settings(
    packageName in Docker := "ama-competition-api"
  )
  .dependsOn(`competition-api`)

lazy val `competition-api` = (project in file("competition/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`common`)

lazy val `web-gateway` = (project in file("web-gateway"))
  .enablePlugins(LagomPlayScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      lagomScaladslAkkaDiscovery,
      macwire,
      scalaTestPlusPlay
    ),
    javaOptions in Test += "-Dconfig.file=test/resources/test.conf"
  )
  .settings(
    packageName in Docker := "ama-web-gateway"
  )
  .dependsOn(`player-api`, `competition-api`, `battle-api`)

lagomCassandraCleanOnStart in ThisBuild := false
