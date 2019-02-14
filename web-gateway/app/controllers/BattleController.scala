package controllers

import com.golemiso.mylagom.battle.api.{Battle, BattleRequest, BattleService}
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class BattleController(mcc: MessagesControllerComponents, service: BattleService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Battle.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { battle =>
      Ok(Json.toJson(battle))
    }.recover {
      case _ : NotFound =>
        NotFound
    }
  }

  def delete(id: Battle.Id): Action[AnyContent] = Action.async { _ =>
    service.delete(id).invoke().map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { _ =>
    service.readAll.invoke.map { players =>
      Ok(Json.toJson(players))
    }
  }

  def post(): Action[BattleRequest] = Action.async(parse.json[BattleRequest]) { request =>
    service.create().invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def patchResult(id: Battle.Id): Action[Battle.Result] = Action.async(parse.json[Battle.Result]) { request =>
    service.updateResult(id).invoke(request.body).map { _ =>
      Accepted
    }
  }
}
