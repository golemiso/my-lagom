package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json._

case class Battle(id: Battle.Id, mode: Battle.Mode, competitors: Seq[Battle.Competitor])
object Battle {
  implicit val format: Format[Battle] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  case class Mode(value: String) extends AnyVal
  object Mode {
    implicit val format: Format[Mode] = Json.valueFormat
  }

  case class Competitor(id: Competitor.Id, players: Seq[Player.Id], result: Option[Result.Id])
  object Competitor {
    implicit val format: Format[Competitor] = Json.format

    case class Id(id: UUID) extends AnyVal
    object Id {
      implicit val format: Format[Id] = Json.valueFormat
    }
  }
}
