package com.golemiso.mylagom.team.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait TeamService extends Service {

  def createNew(): ServiceCall[TeamRequest, Team.Id]
  def read(id: Team.Id): ServiceCall[NotUsed, Team]

  def descriptor: Descriptor = {
    import Service._
    named("teams").withCalls(
      restCall(Method.POST, "/api/teams", createNew _),
        restCall(Method.GET, "/api/teams/:id", read _)
    )
  }
}
