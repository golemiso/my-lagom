package com.golemiso.mylagom.player.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import com.golemiso.mylagom.utils.JsonFormats._

trait PlayerService extends Service {
  def createPlayer: ServiceCall[NewPlayer, Player]
  def getPlayer(playerId: UUID): ServiceCall[NotUsed, Player]

  // Remove once we have a proper player service
  def getPlayers: ServiceCall[NotUsed, Seq[Player]]

  def descriptor = {
    import Service._
    named("player").withCalls(
      pathCall("/api/players", createPlayer),
      pathCall("/api/players/:id", getPlayer _),
      pathCall("/api/players", getPlayers)
    )
  }
}

case class PlayerId(id: UUID) extends AnyVal
object PlayerId {
  implicit  val format: Format[PlayerId] = Json.valueFormat
}

case class PlayerName(name: String) extends AnyVal
object PlayerName {
  implicit  val format: Format[PlayerName] = Json.valueFormat
}

case class Player(id: PlayerId, name: PlayerName)
object Player {
  implicit val format: Format[Player] = Json.format
}

case class NewPlayer(name: PlayerName)
object NewPlayer {
  implicit val format: Format[NewPlayer] = Json.format
}
