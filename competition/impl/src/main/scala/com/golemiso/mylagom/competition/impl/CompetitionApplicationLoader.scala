package com.golemiso.mylagom.competition.impl

import com.golemiso.mylagom.competition.api.CompetitionService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{ LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer }
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

abstract class CompetitionApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
  with AhcWSComponents
  with CassandraPersistenceComponents {

  override lazy val lagomServer: LagomServer = serverFor[CompetitionService](wire[CompetitionServiceImpl])
  override lazy val jsonSerializerRegistry = CompetitionSerializerRegistry

  persistentEntityRegistry.register(wire[CompetitionEntity])
}

class CompetitionApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new CompetitionApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext) =
    new CompetitionApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[CompetitionService])
}
