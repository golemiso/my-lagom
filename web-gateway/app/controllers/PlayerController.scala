package controllers

import com.golemiso.mylagom.player.api.{NewPlayer, PlayerId, PlayerName, PlayerService}
import domain.{Player, PlayerID, PlayerRepository}
import play.api.mvc._
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContext

class PlayerController(mcc: MessagesControllerComponents, playerService: PlayerService, repository: PlayerRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: PlayerId): Action[AnyContent] = Action.async { request: MessagesRequest[AnyContent] =>
    playerService.readPlayer(id).invoke().map { player =>
      val resource = PlayerResource(player.id, player.name)
      Ok(Json.toJson(resource))
    }
  }

  def delete(id: PlayerId): Action[AnyContent] = Action.async { request: MessagesRequest[AnyContent] =>
    repository.deleteBy(PlayerID(id.id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { request: MessagesRequest[AnyContent] =>
    playerService.readAllPlayers.invoke().map { players =>
      val resource = players.map(player => PlayerResource(player.id, player.name))
      Ok(Json.toJson(resource))
    }
  }

  def post(): Action[NewPlayerResource] = Action.async(parse.json[NewPlayerResource]) { request: MessagesRequest[NewPlayerResource] =>
    val playerResource = request.body
    playerService.createPlayer.invoke(NewPlayer(PlayerName(playerResource.name))).map { player =>
      val resource = PlayerResource(player.id, player.name)
      Created(Json.toJson(resource))
    }
  }

  def put(id: PlayerId): Action[PlayerResource] = Action.async(parse.json[PlayerResource]) { request: MessagesRequest[PlayerResource] =>
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

case class PlayerResource(id: PlayerId, name: PlayerName)
object PlayerResource {
  implicit val format: OFormat[PlayerResource] = Json.format[PlayerResource]
  implicit def toEntity(playerResource: PlayerResource): Player = {
    Player(PlayerID(playerResource.id.id), playerResource.name.name)
  }
  implicit def fromEntity(player: Player): PlayerResource = PlayerResource(PlayerId(player.id.value), PlayerName(player.name))
}
