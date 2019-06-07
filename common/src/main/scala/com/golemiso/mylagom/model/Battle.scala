package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json._

case class Battle(id: Battle.Id, mode: Settings.Mode.Id, competitors: Seq[Battle.Competitor])
object Battle {
  implicit val format: Format[Battle] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
    implicit def anyValToUUID(id: Id): UUID = id.id
    implicit def UUIDToAnyVal(id: UUID): Id = Id(id)
  }

  case class Competitor(id: Competitor.Id, players: Seq[Player.Id], result: Option[Settings.Result.Id] = None)
  object Competitor {
    implicit val playerIdformat: Format[Player.Id] = Json.format
    implicit val format: Format[Competitor] = Json.format

    case class Id(id: UUID) extends AnyVal
    object Id {
      implicit val format: Format[Id] = Json.valueFormat
      implicit def anyValToUUID(id: Id): UUID = id.id
      implicit def UUIDToAnyVal(id: UUID): Id = Id(id)
    }
  }
}

case class Settings(
  modes: Seq[Settings.Mode] = Nil,
  participants: Seq[Player.Id] = Nil,
  groupingPatterns: Seq[Settings.GroupingPattern] = Nil,
  results: Seq[Settings.Result] = Nil)
object Settings {
  implicit val format: Format[Settings] = Json.format

  case class Mode(id: Mode.Id, slug: Mode.Slug, name: Mode.Name)
  object Mode {
    implicit val format: Format[Mode] = Json.format

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
    rankBy: RankBy)
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

    case class Group(memberRankings: Seq[Group.Ranking])
    object Group {
      implicit val format: Format[Group] = Json.format

      case class Ranking(ranking: Int) extends AnyVal
      object Ranking {
        implicit val format: Format[Ranking] = Json.valueFormat
      }
    }
  }
}
