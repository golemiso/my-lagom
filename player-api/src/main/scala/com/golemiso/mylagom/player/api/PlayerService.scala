package com.golemiso.mylagom.player.api

import java.util.UUID

import akka.NotUsed
import com.golemiso.mylagom.player.api.Player.PlayerId
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait PlayerService extends Service {
  def createPlayer: ServiceCall[UnpersistedPlayer, Player]
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

//final case class PlayerId(id: UUID) extends AnyVal
//object PlayerId {
//  implicit  val format: Format[PlayerId] = Json.valueFormat
//}

case class PlayerName(name: String) extends AnyVal
object PlayerName {
  implicit  val format: Format[PlayerName] = Json.valueFormat
}

case class PlayerSlug(slug: String) extends AnyVal
object PlayerSlug {
  implicit  val format: Format[PlayerSlug] = Json.valueFormat
}

case class Player(id: PlayerId, name: PlayerName)
object Player {
  implicit val format: Format[Player] = Json.format

  final case class PlayerId(id: UUID) extends AnyVal
  object PlayerId {
    implicit  val format: Format[PlayerId] = Json.valueFormat
  }
}

case class UnpersistedPlayer(name: PlayerName)
object UnpersistedPlayer {
  implicit val format: Format[UnpersistedPlayer] = Json.format
}
