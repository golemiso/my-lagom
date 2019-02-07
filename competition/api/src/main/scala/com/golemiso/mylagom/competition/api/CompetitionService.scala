package com.golemiso.mylagom.competition.api

import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait CompetitionService extends Service {

  def createNew(): ServiceCall[Competition, Competition.Id]

  def descriptor: Descriptor = {
    import Service._
    named("competitions").withCalls(
      restCall(Method.POST, "/api/competitions", createNew _)
    )
  }
}
