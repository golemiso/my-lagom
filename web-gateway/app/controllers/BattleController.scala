package controllers

import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ ExecutionContext, Future }

class BattleController(
  mcc: MessagesControllerComponents,
  service: BattleService,
  competitionService: CompetitionService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  private def fetchCompetition(id: Competition.Id): Future[Competition] = competitionService.read(id).invoke

  def post(competitionId: Competition.Id): Action[JsValue] = Action.async(parse.json) { request =>
    def add[T <: Battle](implicit fjs: Reads[T]) = {
      Json.fromJson[T](request.body) match {
        case JsSuccess(b, _) =>
          service.add(competitionId).invoke(b).map(Ok.apply)
        case JsError(errors) =>
          Future.successful(BadRequest(errors))
      }
    }

    fetchCompetition(competitionId).flatMap { competition =>
      competition.style match {
        case Competition.Style.Group      => add[GroupBattle]
        case Competition.Style.Team       => add[TeamBattle]
        case Competition.Style.Individual => add[IndividualBattle]
        case Competition.Style.Unknown =>
          Future.successful(BadRequest)
      }
    }
  }
}
