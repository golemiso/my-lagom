package com.golemiso.mylagom.competition.api

import java.util.UUID

import akka.NotUsed
import com.golemiso.mylagom.model._
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{ Descriptor, Service, ServiceCall }

trait CompetitionService extends Service {

  def create(): ServiceCall[CompetitionRequest, Competition.Id]
  def read(id: UUID): ServiceCall[NotUsed, Competition]

  def readAll: ServiceCall[NotUsed, Seq[Competition]]

  def descriptor: Descriptor = {
    import Service._
    named("competition").withCalls(
      restCall(Method.POST, "/api/competitions", create _),
      restCall(Method.GET, "/api/competitions/:id", read _),
      restCall(Method.GET, "/api/competitions", readAll)
    )
  }
}
