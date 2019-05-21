package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.competition.api.{ CompetitionRequest, CompetitionService }
import com.golemiso.mylagom.model.Competition
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.ExecutionContext

class CompetitionController(
  mcc: MessagesControllerComponents,
  service: CompetitionService,
  battleService: BattleService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def get(id: Competition.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { competition =>
      Ok(Json.toJson(competition))
    }
  }

  def getAll: Action[AnyContent] = Action.async { _ =>
    service.readAll.invoke.map { competitions =>
      Ok(Json.toJson(competitions))
    }
  }

  def post(): Action[CompetitionRequest] = Action.async(parse.json[CompetitionRequest]) { request =>
    service.create().invoke(request.body).map { id =>
      Created(Json.toJson(id)(Json.format))
    }
  }
}
