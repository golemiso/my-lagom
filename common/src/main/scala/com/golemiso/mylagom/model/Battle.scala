package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

sealed abstract class Battle(val id: Battle.Id, val `type`: Battle.Type)

case class TeamBattle(override val id: Battle.Id, mode: Battle.Mode, competitors: Seq[TeamBattle.Competitor])
  extends Battle(id, Battle.Type.TeamBattle)
object TeamBattle {
  implicit val format: Format[TeamBattle] = Json.format

  case class Competitor(team: Team.Id, result: Result)
  object Competitor {
    implicit val format: Format[Competitor] = Json.format
  }

  sealed abstract class Result(val value: String)
  object Result {
    case object Victory extends Result("victory")
    case object Defeat extends Result("defeat")
    case object Unknown extends Result("unknown")
    val all: Seq[Result] = Victory :: Defeat :: Nil
    def apply(value: String): Result = all.find(_.value == value).getOrElse(Unknown)
    def unapply(result: Result): Option[String] = Some(result.value)

    implicit val format: Format[Result] =
      Format(Reads.StringReads.map(Result.apply), Writes.StringWrites.contramap(_.value))
  }
}

case class IndividualBattle(override val id: Battle.Id, mode: Battle.Mode, competitors: Seq[Player.Id])
  extends Battle(id, Battle.Type.IndividualBattle)
object IndividualBattle {
  implicit val format: Format[IndividualBattle] = Json.format

  case class Competitor(player: Player.Id, result: Result)
  object Competitor {
    implicit val format: Format[Competitor] = Json.format
  }

  sealed abstract class Result(val value: String)
  object Result {
    case object Victory extends Result("victory")
    case object Defeat extends Result("defeat")
    case object Unknown extends Result("unknown")
    val all: Seq[Result] = Victory :: Defeat :: Nil
    def apply(value: String): Result = all.find(_.value == value).getOrElse(Unknown)
    def unapply(result: Result): Option[String] = Some(result.value)

    implicit val format: Format[Result] =
      Format(Reads.StringReads.map(Result.apply), Writes.StringWrites.contramap(_.value))
  }
}

object Battle {
  implicit val format: Format[Battle] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
  }

  sealed abstract class Type(val value: String)
  object Type {
    case object TeamBattle extends Type("team_battle")
    case object IndividualBattle extends Type("individual_battle")
    case object Unknown extends Type("unknown")
    def apply(value: String): Type = value match {
      case TeamBattle.value       => TeamBattle
      case IndividualBattle.value => IndividualBattle
      case _                      => Unknown
    }
    def unapply(`type`: Type): Option[String] = Some(`type`.value)
    implicit val format: Format[Type] =
      Format(Reads.StringReads.map(Type.apply), Writes.StringWrites.contramap(_.value))
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

}
