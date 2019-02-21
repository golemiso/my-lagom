package com.golemiso.mylagom.competition.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model.Competition
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class CompetitionServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends CompetitionService {

  private val currentIdsQuery = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def create() = ServiceCall { competition =>
    val id = Competition.Id(UUID.randomUUID())
    refFor(id).ask(CompetitionCommand.Create(competition(id))).map { _ =>
      id
    }
  }

  override def read(id: Competition.Id) = ServiceCall { _ =>
    refFor(id).ask(CompetitionCommand.Read).map {
      case Some(competition) =>
        competition
      case None =>
        throw NotFound(s"Competition with id ${id.id.toString}")
    }
  }

  override def readAll = ServiceCall { _ =>
    // Note this should never make production....
    currentIdsQuery.currentPersistenceIds()
      .filter(_.startsWith("CompetitionEntity|"))
      .mapAsync(4) { id =>
        val entityId = id.split("\\|", 2).last
        registry.refFor[CompetitionEntity](entityId)
          .ask(CompetitionCommand.Read)
      }.collect {
        case Some(player) => player
      }
      .runWith(Sink.seq)
  }

  private def refFor(id: Competition.Id) = registry.refFor[CompetitionEntity](id.id.toString)
}
