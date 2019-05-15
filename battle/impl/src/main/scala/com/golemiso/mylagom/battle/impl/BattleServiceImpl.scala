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

  override def add(competitionId: Competition.Id) = ServiceCall { battle =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleResultsCommand.Add(battle(id)))
  }

  override def readBattleHistories(competitionId: Competition.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.Read)
  }

  override def readBattlesInProgress(competitionId: Competition.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.Read)
  }

  override def delete(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleResultsCommand.Delete).map { _ =>
      NotUsed
    }
  }

  override def updateResults(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { request =>
    refFor(competitionId).ask(BattleResultsCommand.UpdateResults(request.id, request.results)).map { _ =>
      NotUsed
    }
  }

  override def replaceModes(id: Competition.Id): ServiceCall[Seq[Settings.Mode], NotUsed] = ???
  override def addGroupingPattern(id: Competition.Id): ServiceCall[Settings.GroupingPattern, NotUsed] = ???
  override def addResultPattern(id: Competition.Id): ServiceCall[Settings.ResultPattern, NotUsed] = ???

  override def addParticipant(id: Competition.Id) = ServiceCall { participant =>
    refFor(id).ask(BattleResultsCommand.AddParticipant(participant)).map { _ =>
      NotUsed
    }
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
