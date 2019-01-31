package controllers

import java.util.UUID

import domain._
import play.api.mvc._
import play.api.libs.json.{ Json, OFormat }

import scala.concurrent.ExecutionContext

class BattleController(mcc: MessagesControllerComponents, repository: BattleRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolveBy(BattleID(id)).map { battle =>
      Ok(Json.toJson[BattleResource](battle))
    }
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.deleteBy(BattleID(id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolve.map { battles =>
      Ok(Json.toJson[Seq[BattleResource]](battles))
    }
  }

  def post(): Action[BattleResource] = Action.async(parse.json[BattleResource]) { implicit request: MessagesRequest[BattleResource] =>
    val battleResource = request.body
    repository.store(battleResource).map { battle =>
      Created(Json.toJson[BattleResource](battle))
    }
  }

  def put(id: UUID): Action[BattleResource] = Action.async(parse.json[BattleResource]) { implicit request: MessagesRequest[BattleResource] =>
    val battleResource = request.body
    repository.store(battleResource).map { battle =>
      Accepted(Json.toJson[BattleResource](battle))
    }
  }
}

case class BattleResource(id: Option[UUID], teams: Seq[TeamResource], result: Option[BattleResultResource], mode: String)
object BattleResource {
  implicit val format: OFormat[BattleResource] = Json.format[BattleResource]
  implicit def toEntity(battleResource: BattleResource): Battle = {
    val id = battleResource.id.map(BattleID.apply).getOrElse(BattleID.generate)
    Battle(id, battleResource.teams, battleResource.result, BattleMode(battleResource.mode))
  }
  implicit def fromEntity(battle: Battle): BattleResource = BattleResource(Some(battle.id.value), battle.teams, battle.result, battle.mode.value)
}

case class BattleResultResource(victory: UUID, defeat: UUID)
object BattleResultResource {
  implicit val format: OFormat[BattleResultResource] = Json.format[BattleResultResource]
  implicit def toEntity(battle: Option[BattleResultResource]): Option[BattleResult] = battle.map(b => BattleResult(TeamID(b.victory), TeamID(b.defeat)))
  implicit def fromEntity(battle: Option[BattleResult]): Option[BattleResultResource] = battle.map(b => BattleResultResource(b.victory.value, b.defeat.value))
}
