package com.golemiso.mylagom.model

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

case class Competition(
  id: Competition.Id,
  slug: Competition.Slug,
  name: Competition.Name,
  schedule: Competition.Schedule,
  style: Competition.Style)
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

  sealed abstract class Style(val value: String)
  object Style {
    case object Group extends Style("group")
    case object Team extends Style("team")
    case object Individual extends Style("individual")
    case object Unknown extends Style("unknown")

    val all: Seq[Style] = Group :: Team :: Individual :: Nil
    def apply(value: String): Style = all.find(_.value == value).getOrElse(Unknown)
    def unapply(result: Style): Option[String] = Some(result.value)

    implicit val format: Format[Style] =
      Format(Reads.StringReads.map(Style.apply), Writes.StringWrites.contramap(_.value))
  }
}
