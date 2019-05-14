package com.golemiso.mylagom.model

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

case class Competition(
  id: Competition.Id,
  slug: Competition.Slug,
  name: Competition.Name,
  schedule: Competition.Schedule,
  settings: Competition.Settings)
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

  case class Settings(
    participants: Seq[Player],
    groupingPatterns: Seq[GroupingPattern],
    resultPatterns: Seq[ResultPattern])
  object Settings {
    implicit val format: Format[Settings] = Json.format
  }
}

case class ResultPattern(id: ResultPattern.Id, good: Result, bad: Result)
object ResultPattern {
  implicit val format: Format[ResultPattern] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }
}

case class Result(id: Result.Id, name: Result.Name, point: Result.Point)
object Result {
  implicit val format: Format[Result] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  case class Name(name: String) extends AnyVal
  object Name {
    implicit val format: Format[Name] = Json.valueFormat
  }

  case class Point(point: Int) extends AnyVal
  object Point {
    implicit val format: Format[Point] = Json.valueFormat
  }
}

case class GroupingPattern(
  id: GroupingPattern.Id,
  name: GroupingPattern.Name,
  groups: Seq[GroupingPattern.Group],
  rankBy: GroupingPattern.RankBy)
object GroupingPattern {
  implicit val format: Format[GroupingPattern] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  case class Name(name: String) extends AnyVal
  object Name {
    implicit val format: Format[Name] = Json.valueFormat
  }

  case class Group(number: Group.Number, MemberRankings: Seq[Group.Ranking])
  object Group {
    implicit val format: Format[Group] = Json.format

    case class Number(number: Int) extends AnyVal
    object Number {
      implicit val format: Format[Number] = Json.valueFormat
    }

    case class Ranking(ranking: Int) extends AnyVal
    object Ranking {
      implicit val format: Format[Ranking] = Json.valueFormat
    }
  }

  sealed abstract class RankBy(val value: String)
  object RankBy {
    case object ModeResult extends RankBy("mode_result")
    case object AllResult extends RankBy("all_result")
    case object Unknown extends RankBy("unknown")
    val all: Seq[RankBy] = ModeResult :: AllResult :: Nil
    def apply(value: String): RankBy = all.find(_.value == value).getOrElse(Unknown)
    def unapply(mode: RankBy): Option[String] = Some(mode.value)

    implicit val format: Format[RankBy] =
      Format(Reads.StringReads.map(RankBy.apply), Writes.StringWrites.contramap(_.value))
  }
}
