package com.golemiso.mylagom.battle.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class Battle(id: Battle.Id, slug: Battle.Slug, name: Battle.Name)
object Battle {
  implicit val format: Format[Battle] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit  val format: Format[Id] = Json.valueFormat
  }

  case class Slug(slug: String) extends AnyVal
  object Slug {
    implicit  val format: Format[Slug] = Json.valueFormat
  }

  case class Name(name: String) extends AnyVal
  object Name {
    implicit  val format: Format[Name] = Json.valueFormat
  }
}

case class BattleRequest(slug: Battle.Slug, name: Battle.Name) {
  def apply(id: Battle.Id) = Battle(id, slug, name)
}
object BattleRequest {
  implicit val format: Format[BattleRequest] = Json.format
}
