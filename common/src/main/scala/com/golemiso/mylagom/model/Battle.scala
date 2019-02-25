package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

case class Battle(
  id: Battle.Id,
  competition: Competition.Id,
  mode: Battle.Mode,
  competitors: Battle.Competitors,
  result: Option[Battle.Result]) {
  def apply(result: Battle.Result): Battle = copy(result = Some(result))
}
object Battle {
  implicit val format: Format[Battle] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  sealed abstract class Mode(val value: String)
  object Mode {
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

  case class Competitors(left: Team.Id, right: Team.Id)
  object Competitors {
    implicit val format: Format[Competitors] = Json.format
  }

  sealed abstract class Result(val value: String)
  object Result {
    case object VictoryLeft extends Result("victory_left")
    case object VictoryRight extends Result("victory_right")
    case object Unknown extends Result("unknown")
    val all: Seq[Result] = VictoryLeft :: VictoryRight :: Nil
    def apply(value: String): Result = all.find(_.value == value).getOrElse(Unknown)
    def unapply(result: Result): Option[String] = Some(result.value)

    implicit val format: Format[Result] =
      Format(Reads.StringReads.map(Result.apply), Writes.StringWrites.contramap(_.value))
  }
}
