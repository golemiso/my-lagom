package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

case class Battle(id: Battle.Id, slug: Battle.Slug, name: Battle.Name, mode: Battle.Mode, teams: Seq[Team.Id], result: Option[Battle.Result]) {
  def apply(result: Battle.Result): Battle = copy(result = Some(result))
}
object Battle {
  implicit val format: Format[Battle] = Json.format

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

  sealed abstract class Mode(val value: String)
  object Mode extends {
    case object TurfWar extends Mode("turf_war")
    case object SplatZones extends Mode("splat_zones")
    case object TowerControl extends Mode("tower_control")
    case object Rainmaker extends Mode("rainmaker")
    case object ClamBlitz extends Mode("clam_blitz")
    case object Unknown extends Mode("unknown")
    val all: Seq[Mode] = TurfWar :: SplatZones :: TowerControl :: Rainmaker :: ClamBlitz :: Nil
    def apply(value: String): Mode = all.find(_.value == value).getOrElse(Unknown)
    def unapply(mode: Mode): Option[String] = Some(mode.value)

    implicit val format: Format[Mode] =
      Format(Reads.StringReads.map(Mode.apply), Writes.StringWrites.contramap(_.value))
  }

  case class Result(victory: Team.Id, defeat: Team.Id)
  object Result {
    implicit val format: Format[Result] = Json.format
  }
}
