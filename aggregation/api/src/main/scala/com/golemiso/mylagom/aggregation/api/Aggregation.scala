package com.golemiso.mylagom.aggregation.api

import com.golemiso.mylagom.model.{ Battle, Competition, Player, Team }
import play.api.libs.json.{ Format, Json }

case class CompetitionDetails(
  id: Competition.Id,
  slug: Competition.Slug,
  name: Competition.Name,
  schedule: Competition.Schedule,
  participants: Seq[Player],
  battleHistories: Seq[Battle])
case class PlayerDetails(id: Player.Id)

case class BattleDetails(id: Battle.Id, competition: Competition, mode: Battle.Mode, competitors: BattleDetails.Competitors, result: Option[Battle.Result])
object BattleDetails {
  implicit val format: Format[BattleDetails] = Json.format

  case class Competitors(left: TeamDetails, right: TeamDetails)
  object Competitors {
    implicit val format: Format[Competitors] = Json.format
  }

  case class TeamDetails(id: Team.Id, slug: Team.Slug, name: Team.Name, players: Seq[Player])
  object TeamDetails {
    implicit val format: Format[TeamDetails] = Json.format
  }
}

case class PlayerRanking(rank: Int, player: Player)
object PlayerRanking {
  implicit val format: Format[PlayerRanking] = Json.format
}
