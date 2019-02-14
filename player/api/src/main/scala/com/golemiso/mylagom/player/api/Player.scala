package com.golemiso.mylagom.player.api

import java.util.UUID

import play.api.libs.json.{ Format, Json }

case class Player(id: Player.Id, slug: Player.Slug, name: Player.Name)
object Player {
  implicit val format: Format[Player] = Json.format

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

case class PlayerRequest(slug: Player.Slug, name: Player.Name) {
  def apply(id: Player.Id) = Player(id, slug, name)
}
object PlayerRequest {
  implicit val format: Format[PlayerRequest] = Json.format
}
