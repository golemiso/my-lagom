package controllers

import com.golemiso.mylagom.player.api._
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class PlayerController(mcc: MessagesControllerComponents, service: PlayerService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Player.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { player =>
        Ok(Json.toJson(player))
    }.recover {
      case _ : NotFound =>
        NotFound
    }
  }

  def delete(id: Player.Id): Action[AnyContent] = Action.async { _ =>
    service.delete(id).invoke().map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { _ =>
    service.readAll.invoke.map { players =>
      Ok(Json.toJson(players))
    }
  }

  def post(): Action[PlayerRequest] = Action.async(parse.json[PlayerRequest]) { request =>
    service.create().invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def put(id: Player.Id): Action[PlayerRequest] = Action.async(parse.json[PlayerRequest]) { request =>
    service.update(id).invoke(request.body).map { id =>
      Accepted
    }
  }
}

case class NewPlayerResource(name: String)
object NewPlayerResource {
  implicit val format: play.api.libs.json.Format[NewPlayerResource] = Json.format[NewPlayerResource]
}

case class PlayerResource(id: Player.Id, name: Player.Name)
object PlayerResource {
  implicit val format: play.api.libs.json.Format[PlayerResource] = Json.format[PlayerResource]
  implicit def toEntity(playerResource: PlayerResource): domain.Player = {
    domain.Player(domain.PlayerID(playerResource.id.id), playerResource.name.name)
  }
  implicit def fromEntity(player: domain.Player): PlayerResource = PlayerResource(Player.Id(player.id.value), Player.Name(player.name))
}
