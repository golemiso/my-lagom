package com.golemiso.mylagom.aggregation.impl

import akka.NotUsed
import akka.stream.Materializer
import com.golemiso.mylagom.aggregation.api.{ AggregationService, BattleDetails, PlayerRanking }
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model.{ Battle, Competition, Player, Team }
import com.golemiso.mylagom.player.api.PlayerService
import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext

class AggregationServiceImpl(
  playerService: PlayerService,
  teamService: TeamService,
  battleService: BattleService,
  competitionService: CompetitionService)(implicit ec: ExecutionContext, mat: Materializer)
  extends AggregationService {

  override def battleDetails(id: Battle.Id): ServiceCall[NotUsed, Competition] = ???

  override def teamDetails(id: Team.Id): ServiceCall[NotUsed, Competition] = ???

  override def playerDetails(id: Player.Id): ServiceCall[NotUsed, Competition] = ???
}
