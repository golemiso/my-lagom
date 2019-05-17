package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.model.{ Competition, Player }
import play.api.libs.json.Json
import play.api.mvc.{ Action, MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.ExecutionContext

class SettingController(mcc: MessagesControllerComponents, battleService: BattleService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def postMode(competitionId: Competition.Id): Action[ModeRequest] = Action.async(parse.json[ModeRequest]) { request =>
    battleService.addMode(competitionId).invoke(request.body).map { id =>
      Created(Json.toJson(id))
    }
  }

  def postParticipant(competitionId: Competition.Id): Action[Player.Id] = Action.async(parse.json[Player.Id]) {
    request =>
      battleService.addParticipant(competitionId).invoke(request.body).map { _ =>
        Created
      }
  }

  def postGroupingPattern(competitionId: Competition.Id): Action[GroupingPatternRequest] =
    Action.async(parse.json[GroupingPatternRequest]) { request =>
      battleService.addGroupingPattern(competitionId).invoke(request.body).map { id =>
        Created(Json.toJson(id))
      }
    }

  def postResult(competitionId: Competition.Id): Action[ResultRequest] = Action.async(parse.json[ResultRequest]) {
    request =>
      battleService.addResult(competitionId).invoke(request.body).map { id =>
        Created(Json.toJson(id))
      }
  }
}
