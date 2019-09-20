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
  def readModes(competitionId: UUID): ServiceCall[NotUsed, Seq[Settings.Mode]]
  def removeMode(competitionId: UUID, modeId: UUID): ServiceCall[Settings.Mode.Id, NotUsed]
  def addParticipant(competitionId: UUID): ServiceCall[Player.Id, NotUsed]
  def readParticipants(competition: UUID): ServiceCall[NotUsed, Seq[Player.Id]]
  def removeParticipant(competitionId: UUID, playerId: UUID): ServiceCall[NotUsed, NotUsed]
  def addGroupingPattern(competitionId: UUID): ServiceCall[GroupingPatternRequest, Settings.GroupingPattern.Id]
  def readGroupingPatterns(competition: UUID): ServiceCall[NotUsed, Seq[Settings.GroupingPattern]]
  def addResult(competitionId: UUID): ServiceCall[ResultRequest, Settings.Result.Id]
  def readResults(competition: UUID): ServiceCall[NotUsed, Seq[Settings.Result]]

  def getNewGroups(
    competitionId: UUID,
    modeId: UUID,
    groupPatternId: Option[UUID]): ServiceCall[NotUsed, Seq[Battle.Competitor]]

  def readRankings(competitionId: UUID): ServiceCall[NotUsed, PlayerRankings]

  //  def events: Topic[BattleEvent]

  def descriptor: Descriptor = {
    import Service._
    named("battle")
      .withCalls(
        restCall(Method.POST, "/api/competitions/:competitionId/battles", addBattle _),
        restCall(Method.GET, "/api/competitions/:competitionId/battles", readAllBattles _),
        restCall(Method.DELETE, "/api/competitions/:competitionId/battles/:id", deleteBattle _),
        restCall(Method.PATCH, "/api/competitions/:competitionId/battles/:id/result", updateBattleResults _),
        restCall(
          Method.GET,
          "/api/competitions/:competitionId/battles/new-groups?modeId&groupPatternId",
          getNewGroups _),
        restCall(Method.PUT, "/api/competitions/:competitionId/settings/modes", addMode _),
        restCall(Method.GET, "/api/competitions/:competitionId/settings/modes", readModes _),
        restCall(Method.DELETE, "/api/competitions/:competitionId/settings/modes/:modeId", removeMode _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/participants", addParticipant _),
        restCall(Method.GET, "/api/competitions/:competitionId/settings/participants", readParticipants _),
        restCall(
          Method.DELETE,
          "/api/competitions/:competitionId/settings/participants/:playerId",
          removeParticipant _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/grouping-patterns", addGroupingPattern _),
        restCall(Method.GET, "/api/competitions/:competitionId/settings/grouping-patterns", readGroupingPatterns _),
        restCall(Method.POST, "/api/competitions/:competitionId/settings/results", addResult _),
        restCall(Method.GET, "/api/competitions/:competitionId/settings/results", readResults _),
        restCall(Method.GET, "/api/competitions/:competitionId/rankings", readRankings _)
      ).withAutoAcl(true)
    //      .withTopics(topic("BattleEvent", this.events))
  }
}
