package com.golemiso.mylagom.model

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{ Format, Json }

case class Competition(
  id: Competition.Id,
  slug: Competition.Slug,
  name: Competition.Name,
  schedule: Competition.Schedule)
object Competition {
  implicit val format: Format[Competition] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
    implicit def anyValToUUID(id: Id): UUID = id.id
    implicit def UUIDToAnyVal(id: UUID): Id = Id(id)
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
