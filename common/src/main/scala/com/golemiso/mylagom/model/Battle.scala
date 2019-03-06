package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json._

sealed trait Battle
object Battle {
  implicit val format: Format[Battle] = julienrf.json.derived.flat.oformat((__ \ "type").format[String])

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
}

case class Result(id: Result.Id, name: Result.Name, point: Int)
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
}

case class TeamBattle(id: Battle.Id, mode: Battle.Mode, competitors: Seq[TeamBattle.Competitor]) extends Battle
object TeamBattle {
  implicit val format: Format[TeamBattle] = Json.format

  case class Competitor(team: Team.Id, result: Option[Result.Id])
  object Competitor {
    implicit val format: Format[Competitor] = Json.format

    case class Id(id: UUID) extends AnyVal
    object Id {
      implicit val format: Format[Id] = Json.valueFormat
    }
  }
}

case class IndividualBattle(id: Battle.Id, mode: Battle.Mode, competitors: Seq[Player.Id]) extends Battle
object IndividualBattle {
  implicit val format: Format[IndividualBattle] = Json.format

  case class Competitor(player: Player.Id, result: Option[Result.Id])
  object Competitor {
    implicit val format: Format[Competitor] = Json.format

    case class Id(id: UUID) extends AnyVal
    object Id {
      implicit val format: Format[Id] = Json.valueFormat
    }
  }
}

case class GroupBattle(id: Battle.Id, mode: Battle.Mode, competitors: Seq[GroupBattle.Competitor]) extends Battle
object GroupBattle {
  implicit val format: Format[GroupBattle] = Json.format

  case class Competitor(players: Seq[Player.Id], result: Option[Result.Id])
  object Competitor {
    implicit val format: Format[Competitor] = Json.format
  }
}
