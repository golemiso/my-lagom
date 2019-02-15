package com.golemiso.mylagom.player.api

import com.golemiso.mylagom.model.Player
import play.api.libs.json.{ Format, Json }

case class PlayerRequest(slug: Player.Slug, name: Player.Name) {
  def apply(id: Player.Id) = Player(id, slug, name)
}
object PlayerRequest {
  implicit val format: Format[PlayerRequest] = Json.format
}
