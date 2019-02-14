package com.golemiso.mylagom.battle.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait BattleService extends Service {
  def create(): ServiceCall[BattleRequest, Battle.Id]
  def read(id: Battle.Id): ServiceCall[NotUsed, Battle]
  def delete(id: Battle.Id): ServiceCall[NotUsed, NotUsed]

  def readAll: ServiceCall[NotUsed, Seq[Battle]]
  def updateResult(id: Battle.Id): ServiceCall[Battle.Result, NotUsed]

  def events: Topic[BattleEvent]

  def descriptor: Descriptor = {
    import Service._
    named("battles").withCalls(
      restCall(Method.POST, "/api/battles", create _),
      restCall(Method.GET, "/api/battles/:id", read _),
      restCall(Method.DELETE, "/api/battles/:id", delete _),

      restCall(Method.GET, "/api/battles", readAll),
      restCall(Method.PATCH, "/api/battles/:id/result", updateResult _)).withTopics(
        topic("BattleEvent", this.events))
  }
}
