package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model._
import play.api.libs.json.{ Format, Json }

case class BattleRequest(mode: Battle.Mode, competitors: Seq[Battle.Competitor]) {
  def apply(id: Battle.Id): Battle = Battle(id, mode, competitors)
}
object BattleRequest {
  implicit val format: Format[BattleRequest] = Json.format
}

case class BattleResultRequest(id: Battle.Id, results: Seq[BattleResultRequest.CompetitorResultPair])
object BattleResultRequest {
  implicit val format: Format[BattleResultRequest] = Json.format

  case class CompetitorResultPair(competitor: Battle.Competitor.Id, result: Result.Id)
  object CompetitorResultPair {
    implicit val format: Format[CompetitorResultPair] = Json.format
  }
}
