package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.model.{ Competition, Player }
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.ExecutionContext

class SettingController(mcc: MessagesControllerComponents, battleService: BattleService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def postMode(competitionId: Competition.Id): Action[ModeRequest] = Action.async(parse.json[ModeRequest]) { request =>
    battleService.addMode(competitionId).invoke(request.body).map { id =>
      Created(Json.toJson(id)(Json.format))
    }
  }

  def getModes(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    battleService.readModes(competitionId).invoke.map { modes =>
      Ok(Json.toJson(modes))
    }
  }

  def postParticipant(competitionId: Competition.Id): Action[Player.Id] =
    Action.async(parse.json[Player.Id](Json.format)) { request =>
      battleService.addParticipant(competitionId).invoke(request.body).map { _ =>
        Created
      }
    }

  def getParticipants(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    battleService.readParticipants(competitionId).invoke.map { participants =>
      Ok(Json.toJson(participants))
    }
  }

  def deleteParticipant(competitionId: Competition.Id, participant: Player.Id): Action[AnyContent] = Action.async { _ =>
    battleService.removeParticipant(competitionId, participant).invoke.map { _ =>
      NoContent
    }
  }

  def postGroupingPattern(competitionId: Competition.Id): Action[GroupingPatternRequest] =
    Action.async(parse.json[GroupingPatternRequest]) { request =>
      battleService.addGroupingPattern(competitionId).invoke(request.body).map { id =>
        Created(Json.toJson(id)(Json.format))
      }
    }

  def getGroupingPatterns(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    battleService.readGroupingPatterns(competitionId).invoke.map { groupingPatterns =>
      Created(Json.toJson(groupingPatterns))
    }
  }

  def postResult(competitionId: Competition.Id): Action[ResultRequest] = Action.async(parse.json[ResultRequest]) {
    request =>
      battleService.addResult(competitionId).invoke(request.body).map { id =>
        Created(Json.toJson(id)(Json.format))
      }
  }

  def getResults(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    battleService.readResults(competitionId).invoke.map { results =>
      Created(Json.toJson(results))
    }
  }
}
