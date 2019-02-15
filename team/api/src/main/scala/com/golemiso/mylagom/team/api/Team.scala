package com.golemiso.mylagom.team.api

import com.golemiso.mylagom.model.{ Player, Team }
import play.api.libs.json.{ Format, Json }

case class TeamRequest(slug: Team.Slug, name: Team.Name, players: Seq[Player.Id]) {
  def apply(id: Team.Id) = Team(id, slug, name, players)
}
object TeamRequest {
  implicit val format: Format[TeamRequest] = Json.format
}
