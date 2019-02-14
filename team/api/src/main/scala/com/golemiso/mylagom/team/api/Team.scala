package com.golemiso.mylagom.team.api

import java.util.UUID

import com.golemiso.mylagom.player.api.Player
import play.api.libs.json.{ Format, Json }

case class Team(id: Team.Id, slug: Team.Slug, name: Team.Name, players: Seq[Player.Id])
object Team {
  implicit val format: Format[Team] = Json.format

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
}

case class TeamRequest(slug: Team.Slug, name: Team.Name, players: Seq[Player.Id]) {
  def apply(id: Team.Id) = Team(id, slug, name, players)
}
object TeamRequest {
  implicit val format: Format[TeamRequest] = Json.format
}
