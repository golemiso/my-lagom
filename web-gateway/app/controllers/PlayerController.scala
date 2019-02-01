package controllers

import java.util.UUID

import com.golemiso.mylagom.player.api.{NewPlayer, PlayerName, PlayerService}
import domain.{Player, PlayerID, PlayerRepository}
import play.api.mvc._
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContext

class PlayerController(mcc: MessagesControllerComponents, playerService: PlayerService, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

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

  def post(): Action[NewPlayerResource] = Action.async(parse.json[NewPlayerResource]) { request: MessagesRequest[NewPlayerResource] =>
    val playerResource = request.body
    playerService.createPlayer.invoke(NewPlayer(PlayerName(playerResource.name))).map { player =>
      Created
    }
  }

  def put(id: UUID): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { implicit request: MessagesRequest[PlayerResource] =>
    val playerResource = request.body
    repository.store(playerResource).map { player =>
      Accepted(Json.toJson[PlayerResource](player))
    }
  }
}

case class NewPlayerResource(name: String)
object NewPlayerResource {
  implicit val format: OFormat[NewPlayerResource] = Json.format[NewPlayerResource]
}

case class PlayerResource(id: UUID, name: String)
object PlayerResource {
  implicit val format: OFormat[PlayerResource] = Json.format[PlayerResource]
  implicit def toEntity(playerResource: PlayerResource): Player = {
    Player(PlayerID(playerResource.id), playerResource.name)
  }
  implicit def fromEntity(player: Player): PlayerResource = PlayerResource(player.id.value, player.name)
}
