package com.golemiso.mylagom.player.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait PlayerService extends Service {
  def createPlayer: ServiceCall[NewPlayer, Player]
  def readAllPlayers: ServiceCall[NotUsed, Seq[Player]]
  def readPlayer(id: PlayerId): ServiceCall[NotUsed, Player]
  def updatePlayer(id: PlayerId): ServiceCall[Player, Player]
  def deletePlayer(id: PlayerId): ServiceCall[NotUsed, NotUsed]

  def descriptor: Descriptor = {
    import Service._
    named("player").withCalls(
      restCall(Method.POST, "/api/players", createPlayer),
      restCall(Method.GET, "/api/players", readAllPlayers),
      restCall(Method.GET, "/api/players/:id", readPlayer _),
      restCall(Method.PUT, "/api/players/:id", updatePlayer _),
      restCall(Method.DELETE, "/api/players/:id", deletePlayer _)
    )
  }
}

final case class PlayerId(id: UUID) extends AnyVal
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
