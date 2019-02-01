package com.golemiso.mylagom.player.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.player.api
import com.golemiso.mylagom.player.api.{PlayerId, PlayerService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class PlayerServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends PlayerService {

  private val currentIdsQuery = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def createPlayer = ServiceCall { createPlayer =>
    val id = PlayerId(UUID.randomUUID())
    refFor(id).ask(CreatePlayer(createPlayer.name)).map { _ =>
      api.Player(id, createPlayer.name)
    }
  }

  override def getPlayer(playerId: UUID) = ServiceCall { _ =>
    val id = PlayerId(playerId)
    refFor(id).ask(GetPlayer).map {
      case Some(player) =>
        api.Player(id, player.name)
      case None =>
        throw NotFound(s"Player with id $id")
    }
  }

  override def getPlayers = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery.currentPersistenceIds()
      .filter(_.startsWith("PlayerEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[PlayerEntity](entityId)
          .ask(GetPlayer)
          .map(_.map(player => api.Player(PlayerId(UUID.fromString(entityId)), player.name)))
      }.collect {
        case Some(player) => player
      }
      .runWith(Sink.seq)
  }

  private def refFor(id: PlayerId) = registry.refFor[PlayerEntity](id.toString)
}
