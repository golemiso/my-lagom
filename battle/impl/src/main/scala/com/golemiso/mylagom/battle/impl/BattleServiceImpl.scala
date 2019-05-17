package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.model.{ Battle, Competition, Settings }
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class BattleServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(
  implicit ec: ExecutionContext,
  mat: Materializer)
  extends BattleService {

  override def addBattle(competitionId: UUID) = ServiceCall { battle =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddBattle(battle(id)))
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
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddMode(mode(id)))
  }

  override def addParticipant(id: UUID) = ServiceCall { participant =>
    refFor(id).ask(BattleResultsCommand.AddParticipant(participant)).map { _ =>
      NotUsed
    }
  }

  override def addGroupingPattern(competitionId: UUID) = ServiceCall { groupingPattern =>
    val id = Settings.GroupingPattern.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddGroupingPattern(groupingPattern(id)))
  }

  override def addResult(competitionId: UUID) = ServiceCall { result =>
    val id = Settings.Result.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddResult(result(id)))
  }

  def getNewGroups(competitionId: UUID, modeId: UUID, rankBy: String) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.GetNewGroups(modeId, Settings.GroupingPattern.RankBy(rankBy))).map {
      _.map(Battle.Competitor(UUID.randomUUID(), _))
    }
  }

  private def refFor(competitionId: Competition.Id) = registry.refFor[BattleResultsEntity](competitionId.id.toString)
}
