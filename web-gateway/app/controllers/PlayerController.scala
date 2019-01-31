package controllers

import java.util.UUID

import domain.{ Player, PlayerID, PlayerRepository }
import play.api.mvc._
import play.api.libs.json.{ Json, OFormat }

import scala.concurrent.ExecutionContext

class PlayerController(mcc: MessagesControllerComponents, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolveBy(PlayerID(id)).map { player =>
      Ok(Json.toJson[PlayerResource](player))
    }
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.deleteBy(PlayerID(id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolve.map { players =>
      Ok(Json.toJson[Seq[PlayerResource]](players))
    }
  }

  def post(): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { implicit request: MessagesRequest[PlayerResource] =>
    val playerResource = request.body
    repository.store(playerResource).map { player =>
      Created(Json.toJson[PlayerResource](player))
    }
  }

  def put(id: UUID): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { implicit request: MessagesRequest[PlayerResource] =>
    val playerResource = request.body
    repository.store(playerResource).map { player =>
      Accepted(Json.toJson[PlayerResource](player))
    }
  }
}

case class PlayerResource(id: Option[UUID], name: String)
object PlayerResource {
  implicit val format: OFormat[PlayerResource] = Json.format[PlayerResource]
  implicit def toEntity(playerResource: PlayerResource): Player = {
    val id = playerResource.id.map(PlayerID.apply).getOrElse(PlayerID.generate)
    Player(id, playerResource.name)
  }
  implicit def fromEntity(player: Player): PlayerResource = PlayerResource(Some(player.id.value), player.name)
}
