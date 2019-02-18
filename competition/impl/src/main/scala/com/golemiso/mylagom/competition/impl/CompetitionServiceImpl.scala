package com.golemiso.mylagom.competition.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model.Competition
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class CompetitionServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends CompetitionService {
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

  private def refFor(id: Competition.Id) = registry.refFor[CompetitionEntity](id.id.toString)
}
