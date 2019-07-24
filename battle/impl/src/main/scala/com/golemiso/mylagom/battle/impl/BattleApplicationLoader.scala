package com.golemiso.mylagom.battle.impl

import com.golemiso.mylagom.battle.api.BattleService
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

abstract class BattleApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
  with AhcWSComponents
  with CassandraPersistenceComponents {

  override lazy val lagomServer: LagomServer = serverFor[BattleService](wire[BattleServiceImpl])
  override lazy val jsonSerializerRegistry = BattleSerializerRegistry

  persistentEntityRegistry.register(wire[BattleResultsEntity])
}

class BattleApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new BattleApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext) =
    new BattleApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[BattleService])
}
