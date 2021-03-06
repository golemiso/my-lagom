package com.golemiso.mylagom.player.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.model.Player
import com.golemiso.mylagom.player.api.PlayerService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class PlayerServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(
  implicit ec: ExecutionContext,
  mat: Materializer)
  extends PlayerService {

  private val currentIdsQuery =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def create() = ServiceCall { request =>
    val id = Player.Id(UUID.randomUUID())
    refFor(id).ask(PlayerCommand.Create(request(id))).map { _ =>
      id
    }
  }

  override def read(id: UUID) = ServiceCall { _ =>
    refFor(id).ask(PlayerCommand.Read).map {
      case Some(player) =>
        player
      case None =>
        throw NotFound(s"Player with id ${id.toString}")
    }
  }

  override def update(id: UUID) = ServiceCall { request =>
    refFor(id).ask(PlayerCommand.Update(request(id))).map { _ =>
      NotUsed
    }
  }

  override def delete(id: UUID) = ServiceCall { _ =>
    refFor(id).ask(PlayerCommand.Delete).map { _ =>
      NotUsed
    }
  }

  override def readAll = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery
      .currentPersistenceIds()
      .filter(_.startsWith("PlayerEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[PlayerEntity](entityId).ask(PlayerCommand.Read)
      }.collect {
        case Some(player) => player
      }
      .runWith(Sink.seq)
  }

  private def refFor(id: Player.Id) = registry.refFor[PlayerEntity](id.id.toString)
}
