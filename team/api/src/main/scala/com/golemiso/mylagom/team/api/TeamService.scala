package com.golemiso.mylagom.team.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait TeamService extends Service {
  def create(): ServiceCall[TeamRequest, Team.Id]
  def read(id: Team.Id): ServiceCall[NotUsed, Team]
  def update(id: Team.Id): ServiceCall[TeamRequest, NotUsed]
  def delete(id: Team.Id): ServiceCall[NotUsed, NotUsed]

  def readAll: ServiceCall[NotUsed, Seq[Team]]

  def descriptor: Descriptor = {
    import Service._
    named("teams").withCalls(
      restCall(Method.POST, "/api/teams", create _),
      restCall(Method.GET, "/api/teams/:id", read _),
      restCall(Method.POST, "/api/teams/:id", update _),
      restCall(Method.DELETE, "/api/teams/:id", delete _),

      restCall(Method.GET, "/api/teams", readAll)
    )
  }
}
