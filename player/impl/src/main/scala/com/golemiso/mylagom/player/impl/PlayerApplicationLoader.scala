package com.golemiso.mylagom.player.impl

import com.golemiso.mylagom.player.api.PlayerService
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

abstract class PlayerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
  with AhcWSComponents
  with CassandraPersistenceComponents {

  override lazy val lagomServer: LagomServer = serverFor[PlayerService](wire[PlayerServiceImpl])
  override lazy val jsonSerializerRegistry = PlayerSerializerRegistry

  persistentEntityRegistry.register(wire[PlayerEntity])
}

class PlayerApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new PlayerApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext) =
    new PlayerApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PlayerService])
}
