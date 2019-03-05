package com.golemiso.mylagom.battle.api

import akka.NotUsed
import com.golemiso.mylagom.model.{ Battle, Competition }
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait BattleService extends Service {
  def create(competitionId: Competition.Id): ServiceCall[TeamBattleRequest, Battle.Id]
  def read(competitionId: Competition.Id, id: Battle.Id): ServiceCall[NotUsed, Seq[Battle]]
  def delete(competitionId: Competition.Id, id: Battle.Id): ServiceCall[NotUsed, NotUsed]

  def updateResults(competitionId: Competition.Id, id: Battle.Id): ServiceCall[Seq[Battle.Result], NotUsed]

  //  def events: Topic[BattleEvent]

  def descriptor: Descriptor = {
    import Service._
    named("battles").withCalls(
      restCall(Method.POST, "/api/competitions/:competitionId/battles", create _),
      restCall(Method.GET, "/api/competitions/:competitionId/battles/:id", read _),
      restCall(Method.DELETE, "/api/competitions/:competitionId/battles/:id", delete _),
      restCall(Method.PATCH, "/api/competitions/:competitionId/battles/:id/result", updateResults _)
    )
    //      .withTopics(topic("BattleEvent", this.events))
  }
}
