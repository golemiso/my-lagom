package controllers

import java.util.UUID

import domain.{ Team, TeamID, TeamRepository }
import play.api.libs.json.{ Json, OFormat }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class TeamController(mcc: MessagesControllerComponents, repository: TeamRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolveBy(TeamID(id)).map { team =>
      Ok(Json.toJson[TeamResource](team))
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

  def post(): Action[TeamResource] = Action.async(parse.json[TeamResource]) { implicit request: MessagesRequest[TeamResource] =>
    val teamResource = request.body
    repository.store(teamResource).map { team =>
      Created(Json.toJson[TeamResource](team))
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
  implicit def toEntity(teamResource: TeamResource): Team = {
    val id = teamResource.id.map(TeamID.apply).getOrElse(TeamID.generate)
    Team(id, teamResource.players)
  }
  implicit def fromEntity(team: Team): TeamResource = TeamResource(Some(team.id.value), team.players)
}
