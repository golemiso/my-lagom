package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model.{ Battle, Competition, Team }
import play.api.libs.json.{ Format, Json }

case class BattleRequest(slug: Battle.Slug, name: Battle.Name, mode: Battle.Mode, competition: Competition.Id, competitors: Battle.Competitors) {
  def apply(id: Battle.Id) = Battle(id, slug, name, mode, competitors, None)
}
object BattleRequest {
  implicit val format: Format[BattleRequest] = Json.format
}
