package com.golemiso.mylagom.model

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{ Format, Json }

case class Competition(
  id: Competition.Id,
  slug: Competition.Slug,
  name: Competition.Name,
  schedule: Competition.Schedule,
  participants: Seq[Player.Id],
  battleHistories: Seq[Battle.Id]) {

  def addParticipant(playerId: Player.Id): Competition =
    copy(participants = participants :+ playerId)
  def removeParticipant(playerId: Player.Id): Competition =
    copy(participants = participants.filterNot(_ == playerId))

  def addBattle(battleId: Battle.Id): Competition =
    copy(battleHistories = battleHistories :+ battleId)
  def removeBattle(battleId: Battle.Id): Competition =
    copy(battleHistories = battleHistories.filterNot(_ == battleId))
}
object Competition {
  implicit val format: Format[Competition] = Json.format

  case class Id(id: UUID) extends AnyVal
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

  case class Schedule(start: Instant, end: Instant)
  object Schedule {
    implicit val format: Format[Schedule] = Json.format
  }
}
