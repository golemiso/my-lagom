package com.golemiso.mylagom.player.api

import akka.NotUsed
import com.golemiso.mylagom.model.Player
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait PlayerService extends Service {
  def create(): ServiceCall[PlayerRequest, Player.Id]
  def read(id: Player.Id): ServiceCall[NotUsed, Player]
  def update(id: Player.Id): ServiceCall[PlayerRequest, NotUsed]
  def delete(id: Player.Id): ServiceCall[NotUsed, NotUsed]

  def readAll: ServiceCall[NotUsed, Seq[Player]]

  def descriptor: Descriptor = {
    import Service._
    named("players").withCalls(
      restCall(Method.POST, "/api/players", create _),
      restCall(Method.GET, "/api/players/:id", read _),
      restCall(Method.PUT, "/api/players/:id", update _),
      restCall(Method.DELETE, "/api/players/:id", delete _),
      restCall(Method.GET, "/api/players", readAll)
    )
  }
}
