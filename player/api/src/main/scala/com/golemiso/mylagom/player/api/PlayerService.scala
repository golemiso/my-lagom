package com.golemiso.mylagom.player.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait PlayerService extends Service {
  def createPlayer: ServiceCall[UnpersistedPlayer, Player]
  def readAllPlayers: ServiceCall[NotUsed, Seq[Player]]
  def readPlayer(id: PlayerId): ServiceCall[NotUsed, Player]
  def updatePlayer(id: PlayerId): ServiceCall[Player, Player]
  def deletePlayer(id: PlayerId): ServiceCall[NotUsed, NotUsed]

  def descriptor: Descriptor = {
    import Service._
    named("players").withCalls(
      restCall(Method.POST, "/api/players", createPlayer),
      restCall(Method.GET, "/api/players", readAllPlayers),
      restCall(Method.GET, "/api/players/:id", readPlayer _),
      restCall(Method.PUT, "/api/players/:id", updatePlayer _),
      restCall(Method.DELETE, "/api/players/:id", deletePlayer _)
    )
  }
}
