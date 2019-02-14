package controllers

import com.golemiso.mylagom.competition.api.{ Competition, CompetitionRequest, CompetitionService }
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents }

import scala.concurrent.ExecutionContext

class CompetitionController(mcc: MessagesControllerComponents, service: CompetitionService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Competition.Id): Action[AnyContent] = Action.async { _ =>
    service.read(id).invoke.map { competition =>
      Ok(Json.toJson(competition))
    }
  }

  def post(): Action[CompetitionRequest] = Action.async(parse.json[CompetitionRequest]) { request =>
    service.createNew().invoke(request.body).map { id =>
      Ok(Json.toJson(id))
    }
  }
}
