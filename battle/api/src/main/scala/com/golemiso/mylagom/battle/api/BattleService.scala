package com.golemiso.mylagom.battle.api

import akka.NotUsed
import com.golemiso.mylagom.model._
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait BattleService extends Service {
  def add(competitionId: Competition.Id): ServiceCall[BattleRequest, Battle.Id]
  def readBattleHistories(competitionId: Competition.Id): ServiceCall[NotUsed, Seq[Battle]]
  def readBattlesInProgress(competitionId: Competition.Id): ServiceCall[NotUsed, Seq[Battle]]
  def delete(competitionId: Competition.Id, id: Battle.Id): ServiceCall[NotUsed, NotUsed]

  def updateResults(competitionId: Competition.Id, id: Battle.Id): ServiceCall[BattleResultRequest, NotUsed]

  def replaceModes(competitionId: Competition.Id): ServiceCall[Seq[Settings.Mode], NotUsed]
  def addParticipant(competitionId: Competition.Id): ServiceCall[Player.Id, NotUsed]
  def addGroupingPattern(competitionId: Competition.Id): ServiceCall[Settings.GroupingPattern, NotUsed]
  def addResultPattern(competitionId: Competition.Id): ServiceCall[Settings.ResultPattern, NotUsed]

  //  def events: Topic[BattleEvent]

  def descriptor: Descriptor = {
    import Service._
    named("battles")
      .withCalls(
        restCall(Method.POST, "/api/competitions/:competitionId/battles", add _),
        restCall(Method.GET, "/api/competitions/:competitionId/battle-histories", readBattleHistories _),
        restCall(Method.GET, "/api/competitions/:competitionId/battles-in-progress", readBattlesInProgress _),
        restCall(Method.DELETE, "/api/competitions/:competitionId/battles/:id", delete _),
        restCall(Method.PATCH, "/api/competitions/:competitionId/battles/:id/result", updateResults _),
        restCall(Method.PUT, "/api/competitions/:competitionId/settings/modes", replaceModes _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/participants", addParticipant _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/grouping-pattern", addGroupingPattern _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/result-pattern", addResultPattern _)
      ).withAutoAcl(true)
    //      .withTopics(topic("BattleEvent", this.events))
  }
}
