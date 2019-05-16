package com.golemiso.mylagom.battle.api

import akka.NotUsed
import com.golemiso.mylagom.model._
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait BattleService extends Service {
  def addBattle(competitionId: Competition.Id): ServiceCall[BattleRequest, Battle.Id]
  def readAllBattles(competitionId: Competition.Id): ServiceCall[NotUsed, Seq[Battle]]
  def deleteBattle(competitionId: Competition.Id, id: Battle.Id): ServiceCall[NotUsed, NotUsed]

  def updateBattleResults(competitionId: Competition.Id, id: Battle.Id): ServiceCall[BattleResultsRequest, NotUsed]

  def addMode(competitionId: Competition.Id): ServiceCall[ModeRequest, Settings.Mode.Id]
  def addParticipant(competitionId: Competition.Id): ServiceCall[Player.Id, NotUsed]
  def addGroupingPattern(
    competitionId: Competition.Id): ServiceCall[GroupingPatternRequest, Settings.GroupingPattern.Id]
  def addResult(competitionId: Competition.Id): ServiceCall[ResultRequest, Settings.Result.Id]

  //  def events: Topic[BattleEvent]

  def descriptor: Descriptor = {
    import Service._
    named("battles")
      .withCalls(
        restCall(Method.POST, "/api/competitions/:competitionId/battles", addBattle _),
        restCall(Method.GET, "/api/competitions/:competitionId/battles", readAllBattles _),
        restCall(Method.DELETE, "/api/competitions/:competitionId/battles/:id", deleteBattle _),
        restCall(Method.PATCH, "/api/competitions/:competitionId/battles/:id/result", updateBattleResults _),
        restCall(Method.PUT, "/api/competitions/:competitionId/settings/modes", addMode _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/participants", addParticipant _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/grouping-pattern", addGroupingPattern _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/result", addResult _)
      ).withAutoAcl(true)
    //      .withTopics(topic("BattleEvent", this.events))
  }
}
