package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model.Settings.{ GroupingPattern, Mode, Result }
import com.golemiso.mylagom.model._
import play.api.libs.json.{ Format, Json }

case class BattleRequest(mode: Settings.Mode.Id, competitors: Seq[Battle.Competitor]) {
  def apply(id: Battle.Id): Battle = Battle(id, mode, competitors)
}
object BattleRequest {
  implicit val modeIdFormat: Format[Settings.Mode.Id] = Json.format
  implicit val format: Format[BattleRequest] = Json.format
}

case class BattleResultsRequest(id: Battle.Id, results: Seq[BattleResultsRequest.CompetitorResultPair])
object BattleResultsRequest {
  implicit val format: Format[BattleResultsRequest] = Json.format

  case class CompetitorResultPair(id: Battle.Competitor.Id, result: Settings.Result.Id)
  object CompetitorResultPair {
    implicit val format: Format[CompetitorResultPair] = Json.format
  }
}

case class ModeRequest(slug: Mode.Slug, name: Mode.Name) {
  def apply(id: Mode.Id): Mode = Mode(id, slug, name)
}
object ModeRequest {
  implicit val format: Format[ModeRequest] = Json.format
}

case class ResultRequest(name: Result.Name, point: Result.Point) {
  def apply(id: Result.Id) = Result(id, name, point)
}
object ResultRequest {
  implicit val format: Format[ResultRequest] = Json.format
}

case class GroupingPatternRequest(name: GroupingPattern.Name, groups: Seq[GroupingPattern.Group], rankBy: RankBy) {
  def apply(id: GroupingPattern.Id) = GroupingPattern(id, name, groups, rankBy)
}
object GroupingPatternRequest {
  implicit val format: Format[GroupingPatternRequest] = Json.format
}
