package com.golemiso.mylagom.aggregation.api

import akka.NotUsed
import com.golemiso.mylagom.model.{ Battle, Competition, Player, Team }
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait AggregationService extends Service {

  def rankingsBy(id: Competition.Id): ServiceCall[NotUsed, Seq[PlayerRanking]]
  def battleHistoriesBy(id: Competition.Id): ServiceCall[NotUsed, Seq[BattleDetails]]
  def battleDetails(id: Battle.Id): ServiceCall[NotUsed, Competition]
  def teamDetails(id: Team.Id): ServiceCall[NotUsed, Competition]
  def playerDetails(id: Player.Id): ServiceCall[NotUsed, Competition]

  override def descriptor: Descriptor = {
    import Service._
    named("aggregation").withCalls(
      restCall(Method.GET, "/api/competitions/:id/rankings", rankingsBy _),
      restCall(Method.GET, "/api/competitions/:id", battleHistoriesBy _),
      restCall(Method.GET, "/api/battles/:id", battleDetails _),
      restCall(Method.GET, "/api/teams/:id", teamDetails _),
      restCall(Method.GET, "/api/players/:id", playerDetails _))
  }
}
