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
case class BattleDetails(id: Battle.Id, slug: Battle.Slug, name: Battle.Name, mode: Battle.Mode, competitors: BattleDetails.Competitors, result: Option[Battle.Result])
object BattleDetails {
  case class Competitors(left: Seq[Player], right: Seq[Player])
  object Competitors {
    implicit val format: Format[Competitors] = Json.format
  }
}
case class TeamDetails(id: Team.Id)
