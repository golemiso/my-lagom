package com.golemiso.mylagom.competition.api

import akka.NotUsed
import com.golemiso.mylagom.model.{ Competition, Player }
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait CompetitionService extends Service {

  def create(): ServiceCall[CompetitionRequest, Competition.Id]
  def read(id: Competition.Id): ServiceCall[NotUsed, Competition]

  def readAll: ServiceCall[NotUsed, Seq[Competition]]
  def addParticipant(id: Competition.Id): ServiceCall[Player.Id, NotUsed]

  def descriptor: Descriptor = {
    import Service._
    named("competitions").withCalls(
      restCall(Method.POST, "/api/competitions", create _),
      restCall(Method.GET, "/api/competitions/:id", read _),

      restCall(Method.GET, "/api/competitions", readAll),
      restCall(Method.POST, "/api/competitions/:id/participants", addParticipant _))
  }
}
