package com.golemiso.mylagom.team.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.golemiso.mylagom.team.api.Team.Id
import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class TeamServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends TeamService {
  override def createNew() = ServiceCall { team =>
    val id = Id(UUID.randomUUID())
    refFor(id).ask(TeamCommand.Create(team(id))).map { _ =>
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

  private def refFor(id: Id) = registry.refFor[TeamEntity](id.id.toString)
}
