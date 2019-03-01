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
import com.golemiso.mylagom.model.{ Battle, Competition }
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

  private val currentIdsQuery =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def create(competitionId: Competition.Id) = ServiceCall { request =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(competitionId: Competition.Id).ask(BattleCommand.Create(request(id))).map { _ =>
      id
    }
  }

  override def read(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleCommand.Read).map {
      case Some(battle) =>
        battle
      case None =>
        throw NotFound(s"Battle with id ${id.id.toString}")
    }
  }

  override def delete(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { _ =>
    refFor(competitionId).ask(BattleCommand.Delete).map { _ =>
      NotUsed
    }
  }

  override def readAll(competitionId: Competition.Id) = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery
      .currentPersistenceIds()
      .filter(_.startsWith("BattleEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[BattleEntity](entityId).ask(BattleCommand.Read)
      }.collect {
        case Some(battle) => battle
      }
      .runWith(Sink.seq)
  }

  override def updateResult(competitionId: Competition.Id, id: Battle.Id) = ServiceCall { result =>
    refFor(competitionId).ask(BattleCommand.UpdateResult(result)).map { _ =>
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

  private def refFor(competitionId: Competition.Id) = registry.refFor[BattleEntity](competitionId.id.toString)
}
