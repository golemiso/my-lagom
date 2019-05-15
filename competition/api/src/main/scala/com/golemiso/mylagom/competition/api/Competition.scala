package com.golemiso.mylagom.competition.api

import com.golemiso.mylagom.model.Competition
import play.api.libs.json.{ Format, Json }

case class CompetitionRequest(slug: Competition.Slug, name: Competition.Name, schedule: Competition.Schedule) {
  def apply(id: Competition.Id) = Competition(id, slug, name, schedule)
}
object CompetitionRequest {
  implicit val format: Format[CompetitionRequest] = Json.format
}
