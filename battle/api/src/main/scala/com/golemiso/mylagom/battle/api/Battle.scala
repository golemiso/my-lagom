package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model.{ Battle, Competition, Team }
import play.api.libs.json.{ Format, Json }

case class BattleRequest(competition: Competition.Id, mode: Battle.Mode, competitors: Battle.Competitors) {
  def apply(id: Battle.Id) = Battle(id, competition, mode, competitors, None)
}
object BattleRequest {
  implicit val format: Format[BattleRequest] = Json.format
}
