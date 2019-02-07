package controllers

import com.golemiso.mylagom.competition.api.{Competition, CompetitionService}
import play.api.libs.json.Json
import play.api.mvc.{Action, MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.ExecutionContext

class CompetitionController(mcc: MessagesControllerComponents, service: CompetitionService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(id: Competition.Id) = TODO

  def post(): Action[Competition] = Action.async(parse.json[Competition]) { request =>
    service.createNew().invoke(request.body).map { id =>
      Ok(Json.toJson(id))
    }
  }
}
