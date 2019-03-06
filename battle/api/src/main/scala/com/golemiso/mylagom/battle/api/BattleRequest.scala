package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model._
import play.api.libs.json.{ Format, Json }

case class TeamBattleRequest(mode: Battle.Mode, competitors: Seq[TeamBattle.Competitor]) {
  def apply(id: Battle.Id): Battle = TeamBattle(id, mode, competitors)
}
object TeamBattleRequest {
  implicit val format: Format[TeamBattleRequest] = Json.format
}

case class TeamBattleResultRequest(id: Battle.Id, results: Seq[TeamBattleResultRequest.PlayersResultPair])
object TeamBattleResultRequest {
  implicit val format: Format[TeamBattleResultRequest] = Json.format

  case class PlayersResultPair(players: Seq[Player.Id], result: Result.Id)
  object PlayersResultPair {
    implicit val format: Format[PlayersResultPair] = Json.format

  }
}
