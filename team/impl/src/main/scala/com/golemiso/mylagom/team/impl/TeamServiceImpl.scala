package com.golemiso.mylagom.team.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.team.api.Team.Id
import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class TeamServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends TeamService {

  private val currentIdsQuery = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def create() = ServiceCall { request =>
    val id = Id(UUID.randomUUID())
    refFor(id).ask(TeamCommand.Create(request(id))).map { _ =>
      id
    }
  }

  override def read(id: Id) = ServiceCall { _ =>
    refFor(id).ask(TeamCommand.Read).map {
      case Some(team) =>
        team
      case None =>
        throw NotFound(s"Team with id ${id.id.toString}")
    }
  }

  override def update(id: Id) = ServiceCall { request =>
    refFor(id).ask(TeamCommand.Update(request(id))).map { _ => NotUsed }
  }

  override def delete(id: Id) = ServiceCall { _ =>
    refFor(id).ask(TeamCommand.Delete).map { _ => NotUsed }
  }

  override def readAll = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery.currentPersistenceIds()
      .filter(_.startsWith("TeamEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[TeamEntity](entityId)
          .ask(TeamCommand.Read)
      }.collect {
        case Some(team) => team
      }
      .runWith(Sink.seq)
  }

  private def refFor(id: Id) = registry.refFor[TeamEntity](id.id.toString)
}
