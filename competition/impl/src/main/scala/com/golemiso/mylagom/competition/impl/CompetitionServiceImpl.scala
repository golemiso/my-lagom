package com.golemiso.mylagom.competition.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.golemiso.mylagom.competition.api.Competition.Id
import com.golemiso.mylagom.competition.api.CompetitionService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class CompetitionServiceImpl(registry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) extends CompetitionService {
  override def createNew() = ServiceCall { competition =>
    val id = Id(UUID.randomUUID())
    refFor(id).ask(CompetitionCommand.Create(competition)).map { _ =>
      id
    }
  }

  private def refFor(id: Id) = registry.refFor[CompetitionEntity](id.id.toString)
}
