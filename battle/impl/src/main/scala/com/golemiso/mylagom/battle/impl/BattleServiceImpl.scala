package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.battle.api
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.model.{ Battle, Competition, Settings }
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{ EventStreamElement, PersistentEntityRegistry }

import scala.collection.immutable
import scala.concurrent.ExecutionContext

class BattleServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(
  implicit ec: ExecutionContext,
  mat: Materializer)
  extends BattleService {

  override def addBattle(competitionId: Competition.Id) = ServiceCall { battle =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddBattle(battle(id)))
  }

  override def readAllBattles(competitionId: Competition.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.Read)
  }

  override def deleteBattle(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.Delete).map { _ =>
      NotUsed
    }
  }

  override def updateBattleResults(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { request =>
    refFor(competitionId).ask(BattleResultsCommand.UpdateResults(request.id, request.results)).map { _ =>
      NotUsed
    }
  }

  override def addMode(competitionId: Competition.Id) = ServiceCall { mode =>
    val id = Settings.Mode.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddMode(mode(id)))
  }

  override def addParticipant(id: Competition.Id) = ServiceCall { participant =>
    refFor(id).ask(BattleResultsCommand.AddParticipant(participant)).map { _ =>
      NotUsed
    }
  }

  override def addGroupingPattern(competitionId: Competition.Id) = ServiceCall { groupingPattern =>
    val id = Settings.GroupingPattern.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddGroupingPattern(groupingPattern(id)))
  }
  override def addResult(competitionId: Competition.Id) = ServiceCall { result =>
    val id = Settings.Result.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.AddResult(result(id)))
  }

  //  override def events: Topic[api.BattleEvent] = TopicProducer.singleStreamWithOffset { fromOffset =>
  //    registry.eventStream(BattleEvent.Tag, fromOffset).mapConcat {
  //      case EventStreamElement(_, BattleEvent.ResultUpdated(battle), offset) =>
  //        immutable.Seq((api.BattleEvent.ResultUpdated(battle), offset))
  //      case _ => Nil
  //    }
  //  }

  private def refFor(competitionId: Competition.Id) = registry.refFor[BattleResultsEntity](competitionId.id.toString)
}
