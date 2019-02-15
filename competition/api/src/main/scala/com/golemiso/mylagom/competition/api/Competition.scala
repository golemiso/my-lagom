package com.golemiso.mylagom.competition.api

import java.time.Instant
import java.util.UUID

import com.golemiso.mylagom.battle.api.Battle
import com.golemiso.mylagom.player.api.Player
import play.api.libs.json.{Format, Json}

import scala.concurrent.Future

case class Competition(
                        id: Competition.Id,
                        slug: Competition.Slug,
                        name: Competition.Name,
                        schedule: Competition.Schedule,
                        participants: Seq[Player.Id],
                        battleHistories: Seq[Battle.Id])
object Competition {
  implicit val format: Format[Competition] = Json.format

  case class Id(id: UUID) extends AnyVal {
    def fetch(service: CompetitionService): Future[Competition] = service.read(this).invoke
  }
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  case class Slug(slug: String) extends AnyVal
  object Slug {
    implicit val format: Format[Slug] = Json.valueFormat
  }

  case class Name(name: String) extends AnyVal
  object Name {
    implicit val format: Format[Name] = Json.valueFormat
  }

  case class Schedule(open: Instant, close: Instant)
  object Schedule {
    implicit val format: Format[Schedule] = Json.format
  }
}

case class CompetitionRequest(slug: Competition.Slug, name: Competition.Name, schedule: Competition.Schedule) {
  def apply(id: Competition.Id) = Competition(id, slug, name, schedule, Nil, Nil)
}
object CompetitionRequest {
  implicit val format: Format[CompetitionRequest] = Json.format
}
