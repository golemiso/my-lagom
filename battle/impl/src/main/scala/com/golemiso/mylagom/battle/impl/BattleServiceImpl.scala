package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.battle.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class BattleServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends BattleService {

  private val currentIdsQuery = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def create() = ServiceCall { request =>
    val id = Battle.Id(UUID.randomUUID())
    refFor(id).ask(BattleCommand.Create(request(id))).map { _ =>
      id
    }
  }

  override def read(id: Battle.Id) = ServiceCall { _ =>
    refFor(id).ask(BattleCommand.Read).map {
      case Some(battle) =>
        battle
      case None =>
        throw NotFound(s"Battle with id ${id.id.toString}")
    }
  }

  override def update(id: Battle.Id) = ServiceCall { request =>
    refFor(id).ask(BattleCommand.Update(request(id))).map { _ => NotUsed }
  }

  override def delete(id: Battle.Id) = ServiceCall { _ =>
    refFor(id).ask(BattleCommand.Delete).map { _ => NotUsed }
  }

  override def readAll = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery.currentPersistenceIds()
      .filter(_.startsWith("BattleEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[BattleEntity](entityId)
          .ask(BattleCommand.Read)
      }.collect {
      case Some(battle) => battle
    }
      .runWith(Sink.seq)
  }

  private def refFor(id: Battle.Id) = registry.refFor[BattleEntity](id.id.toString)
}
