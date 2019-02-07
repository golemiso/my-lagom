package com.golemiso.mylagom.competition.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class Competition(slug: Competition.Slug, name: Competition.Name)
object Competition {
  implicit  val format: Format[Competition] = Json.format

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