package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.model.{ Battle, Competition, Settings }
import com.lightbend.lagom.scaladsl.api.{ Descriptor, ServiceCall }
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class BattleServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(
  implicit ec: ExecutionContext,
  mat: Materializer)
  extends BattleService {

  override def addBattle(competitionId: UUID) = ServiceCall { battle =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(competitionId).ask(BattleResultsCommand.AddBattle(battle(id)))
  }

  override def readAllBattles(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.ReadBattles)
  }

  override def deleteBattle(competitionId: UUID, id: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.DeleteBattle).map { _ =>
      NotUsed
    }
  }

  override def updateBattleResults(competitionId: UUID, id: UUID) = ServiceCall { request =>
    refFor(competitionId).ask(BattleResultsCommand.UpdateResults(request.id, request.results)).map { _ =>
      NotUsed
    }
  }

  override def addMode(competitionId: UUID) = ServiceCall { mode =>
    val id = Settings.Mode.Id(UUID.randomUUID())
    refFor(competitionId).ask(BattleResultsCommand.AddMode(mode(id)))
  }

  override def readModes(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.ReadSettings).map(_.modes)
  }

  override def removeMode(competitionId: UUID, modeId: UUID) = ServiceCall { mode =>
    refFor(competitionId).ask(BattleResultsCommand.RemoveMode(modeId)).map { _ =>
      NotUsed
    }
  }

  override def addParticipant(competitionId: UUID) = ServiceCall { participant =>
    refFor(competitionId).ask(BattleResultsCommand.AddParticipant(participant)).map { _ =>
      NotUsed
    }
  }

  override def readParticipants(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.ReadSettings).map(_.participants)
  }

  override def removeParticipant(competitionId: UUID, playerId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.RemoveParticipant(playerId)).map { _ =>
      NotUsed
    }
  }

  override def addGroupingPattern(competitionId: UUID) = ServiceCall { groupingPattern =>
    val id = Settings.GroupingPattern.Id(UUID.randomUUID())
    refFor(competitionId).ask(BattleResultsCommand.AddGroupingPattern(groupingPattern(id)))
  }

  override def readGroupingPatterns(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.ReadSettings).map(_.groupingPatterns)
  }

  override def addResult(competitionId: UUID) = ServiceCall { result =>
    val id = Settings.Result.Id(UUID.randomUUID())
    refFor(competitionId).ask(BattleResultsCommand.AddResult(result(id)))
  }

  override def readResults(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.ReadSettings).map(_.results)
  }

  def getNewGroups(competitionId: UUID, modeId: UUID, groupPatternId: Option[UUID]) = ServiceCall { _ =>
    refFor(competitionId)
      .ask(BattleResultsCommand.GetNewGroups(modeId, groupPatternId.map(Settings.GroupingPattern.Id(_)))).map {
        _.map(Battle.Competitor(UUID.randomUUID(), _))
      }
  }

  def readRankings(competitionId: UUID) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.GetRankings)
  }

  private def refFor(competitionId: Competition.Id) = registry.refFor[BattleResultsEntity](competitionId.id.toString)
}
