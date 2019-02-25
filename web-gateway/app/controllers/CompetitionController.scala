package controllers

import com.golemiso.mylagom.aggregation.api.AggregationService
import com.golemiso.mylagom.competition.api.{ CompetitionRequest, CompetitionService }
import com.golemiso.mylagom.model.{ Competition, Player }
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.ExecutionContext

class CompetitionController(
  mcc: MessagesControllerComponents,
  service: CompetitionService,
  aggregationService: AggregationService)(implicit ec: ExecutionContext)
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

  def postParticipant(id: Competition.Id): Action[Player.Id] = Action.async(parse.json[Player.Id]) { request =>
    service.addParticipant(id).invoke(request.body).map { _ =>
      Created
    }
  }

  def getBattleHistories(id: Competition.Id): Action[AnyContent] = Action.async { _ =>
    aggregationService.battleHistoriesBy(id).invoke.map { battles =>
      Ok(Json.toJson(battles))
    }
  }
}
