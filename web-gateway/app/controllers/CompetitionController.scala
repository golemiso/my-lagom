package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.competition.api.{ CompetitionRequest, CompetitionService }
import com.golemiso.mylagom.model.{ Competition, Player }
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
      Created(Json.toJson(id))
    }
  }

  def postMode(id: Competition.Id): Action[ModeRequest] = Action.async(parse.json[ModeRequest]) { request =>
    battleService.addMode(id).invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def postParticipant(id: Competition.Id): Action[Player.Id] = Action.async(parse.json[Player.Id]) { request =>
    battleService.addParticipant(id).invoke(request.body).map { _ =>
      Created
    }
  }

  def postGroupingPattern(id: Competition.Id): Action[GroupingPatternRequest] =
    Action.async(parse.json[GroupingPatternRequest]) { request =>
      battleService.addGroupingPattern(id).invoke(request.body).map { id =>
        Created(Json.toJson(id))
      }
    }

  def postResult(id: Competition.Id): Action[ResultRequest] = Action.async(parse.json[ResultRequest]) { request =>
    battleService.addResult(id).invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }
}
