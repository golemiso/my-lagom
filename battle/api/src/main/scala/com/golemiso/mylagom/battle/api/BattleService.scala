package com.golemiso.mylagom.battle.api

import java.util.UUID

import akka.NotUsed
import com.golemiso.mylagom.model._
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait BattleService extends Service {
  def addBattle(competitionId: UUID): ServiceCall[BattleRequest, Battle.Id]
  def readAllBattles(competitionId: UUID): ServiceCall[NotUsed, Seq[Battle]]
  def deleteBattle(competitionId: UUID, id: UUID): ServiceCall[NotUsed, NotUsed]

  def updateBattleResults(competitionId: UUID, id: UUID): ServiceCall[BattleResultsRequest, NotUsed]

  def addMode(competitionId: UUID): ServiceCall[ModeRequest, Settings.Mode.Id]
  def readMode(competitionId: UUID): ServiceCall[ModeRequest, Settings.Mode.Id]
  def addParticipant(competitionId: UUID): ServiceCall[Player.Id, NotUsed]
  def addGroupingPattern(competitionId: UUID): ServiceCall[GroupingPatternRequest, Settings.GroupingPattern.Id]
  def addResult(competitionId: UUID): ServiceCall[ResultRequest, Settings.Result.Id]

  def getNewGroups(competitionId: UUID, modeId: UUID, rankBy: String): ServiceCall[NotUsed, Seq[Battle.Competitor]]

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
