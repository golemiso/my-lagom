package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class BattleController(
  mcc: MessagesControllerComponents,
  service: BattleService,
  competitionService: CompetitionService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  private def fetchCompetition(id: Competition.Id): Future[Competition] = competitionService.read(id).invoke

  def getAll(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    for {
      battles <- service.readAllBattles(competitionId).invoke
    } yield Ok(Json.toJson(battles))
  }

  def post(competitionId: Competition.Id): Action[BattleRequest] = Action.async(parse.json[BattleRequest]) { request =>
    for {
      _ <- service.addBattle(competitionId).invoke(request.body)
    } yield Ok
  }

  def patchResults(competitionId: Competition.Id, id: Battle.Id): Action[BattleResultsRequest] =
    Action.async(parse.json[BattleResultsRequest]) { request =>
      for {
        _ <- service.updateBattleResults(competitionId, id).invoke(request.body)
      } yield Ok
    }
}
