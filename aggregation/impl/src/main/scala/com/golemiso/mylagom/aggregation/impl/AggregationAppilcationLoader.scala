package com.golemiso.mylagom.aggregation.impl

import com.golemiso.mylagom.aggregation.api.AggregationService
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.player.api.PlayerService
import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{ LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer }
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

abstract class AggregationApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
  with AhcWSComponents {

  override lazy val lagomServer: LagomServer = serverFor[AggregationService](wire[AggregationServiceImpl])

  lazy val playerService: PlayerService = serviceClient.implement[PlayerService]
  lazy val teamService: TeamService = serviceClient.implement[TeamService]
  lazy val battleService: BattleService = serviceClient.implement[BattleService]
  lazy val competitionService: CompetitionService = serviceClient.implement[CompetitionService]
}

class AggregationApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new AggregationApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext) =
    new AggregationApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[AggregationService])
}
