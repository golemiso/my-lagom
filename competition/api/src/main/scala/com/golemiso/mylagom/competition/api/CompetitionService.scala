package com.golemiso.mylagom.competition.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait CompetitionService extends Service {

  def createNew(): ServiceCall[CompetitionRequest, Competition.Id]
  def read(id: Competition.Id): ServiceCall[NotUsed, Competition]

  def descriptor: Descriptor = {
    import Service._
    named("competitions").withCalls(
      restCall(Method.POST, "/api/competitions", createNew _),
      restCall(Method.GET, "/api/competitions/:id", read _))
  }
}
