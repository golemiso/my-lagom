package controllers

import java.util.UUID

import com.golemiso.mylagom.team.api.{Team, TeamRequest, TeamService}
import domain.{TeamID, TeamRepository, Team => DomainTeam}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TeamController(mcc: MessagesControllerComponents, service: TeamService, repository: TeamRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Team.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { team =>
      Ok(Json.toJson(team))
    }
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.deleteBy(TeamID(id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolve.map { teams =>
      Ok(Json.toJson[Seq[TeamResource]](teams))
    }
  }

  def post(): Action[TeamRequest] = Action.async(parse.json[TeamRequest]) { request =>
    service.createNew().invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def put(id: UUID): Action[TeamResource] = Action.async(parse.json[TeamResource]) { implicit request: MessagesRequest[TeamResource] =>
    val teamResource = request.body
    repository.store(teamResource).map { team =>
      Accepted(Json.toJson[TeamResource](team))
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
