package com.golemiso.mylagom.player.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class Player(id: PlayerId, name: PlayerName)
object Player {
  implicit val format: Format[Player] = Json.format
}

case class PlayerId(id: UUID) extends AnyVal
object PlayerId {
  implicit  val format: Format[PlayerId] = Json.valueFormat
}

case class PlayerSlug(slug: String) extends AnyVal
object PlayerSlug {
  implicit  val format: Format[PlayerSlug] = Json.valueFormat
}

case class PlayerName(name: String) extends AnyVal
object PlayerName {
  implicit  val format: Format[PlayerName] = Json.valueFormat
}

case class UnpersistedPlayer(name: PlayerName)
object UnpersistedPlayer {
  implicit val format: Format[UnpersistedPlayer] = Json.format
}
