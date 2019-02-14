package controllers

import java.util.UUID

import com.golemiso.mylagom.team.api.{ Team, TeamRequest, TeamService }
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import domain.{ TeamID, Team => DomainTeam }
import play.api.libs.json.{ Json, OFormat }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TeamController(mcc: MessagesControllerComponents, service: TeamService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Team.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { team =>
      Ok(Json.toJson(team))
    }.recover {
      case _: NotFound =>
        NotFound
    }
  }

  def delete(id: Team.Id): Action[AnyContent] = Action.async { _ =>
    service.delete(id).invoke().map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { _ =>
    service.readAll.invoke.map { teams =>
      Ok(Json.toJson(teams))
    }
  }

  def post(): Action[TeamRequest] = Action.async(parse.json[TeamRequest]) { request =>
    service.create().invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def put(id: Team.Id): Action[TeamRequest] = Action.async(parse.json[TeamRequest]) { request =>
    service.update(id).invoke(request.body).map { id =>
      Accepted
    }
  }
}

case class TeamResource(id: Option[UUID], players: Seq[PlayerResource])
object TeamResource {
  implicit val format: OFormat[TeamResource] = Json.format[TeamResource]
  implicit def toEntity(teamResource: TeamResource): DomainTeam = {
    val id = teamResource.id.map(TeamID.apply).getOrElse(TeamID.generate)
    DomainTeam(id, teamResource.players)
  }
  implicit def fromEntity(team: DomainTeam): TeamResource = TeamResource(Some(team.id.value), team.players)
}
