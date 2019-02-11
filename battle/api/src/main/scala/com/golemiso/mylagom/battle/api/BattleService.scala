package com.golemiso.mylagom.battle.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait BattleService extends Service {
  def create(): ServiceCall[BattleRequest, Battle.Id]
  def read(id: Battle.Id): ServiceCall[NotUsed, Battle]
  def update(id: Battle.Id): ServiceCall[BattleRequest, NotUsed]
  def delete(id: Battle.Id): ServiceCall[NotUsed, NotUsed]

  def readAll: ServiceCall[NotUsed, Seq[Battle]]

  def descriptor: Descriptor = {
    import Service._
    named("battles").withCalls(
      restCall(Method.POST, "/api/battles", create _),
      restCall(Method.GET, "/api/battles/:id", read _),
      restCall(Method.PUT, "/api/battles/:id", update _),
      restCall(Method.DELETE, "/api/battles/:id", delete _),

      restCall(Method.GET, "/api/battles", readAll)
    )
  }
}
