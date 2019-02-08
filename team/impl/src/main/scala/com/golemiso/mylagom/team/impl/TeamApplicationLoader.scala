package com.golemiso.mylagom.team.impl

import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

abstract class TeamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with CassandraPersistenceComponents {

  override lazy val lagomServer: LagomServer = serverFor[TeamService](wire[TeamServiceImpl])
  override lazy val jsonSerializerRegistry = TeamSerializerRegistry

  persistentEntityRegistry.register(wire[TeamEntity])
}

class TeamApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new TeamApplication(context)  {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext) =
    new TeamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[TeamService])
}
