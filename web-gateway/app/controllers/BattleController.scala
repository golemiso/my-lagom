package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.model._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class BattleController(mcc: MessagesControllerComponents, service: BattleService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def getAll(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    for {
      battles <- service.readAllBattles(competitionId).invoke
    } yield Ok(Json.toJson(battles))
  }

  def post(competitionId: Competition.Id): Action[BattleRequest] = Action.async(parse.json[BattleRequest]) { request =>
    for {
      id <- service.addBattle(competitionId).invoke(request.body)
    } yield Ok(Json.toJson(id)(Json.format))
  }

  def patchResults(competitionId: Competition.Id, id: Battle.Id): Action[BattleResultsRequest] =
    Action.async(parse.json[BattleResultsRequest]) { request =>
      for {
        _ <- service.updateBattleResults(competitionId, id).invoke(request.body)
      } yield Ok
    }

  def getNewGroups(
    competitionId: Competition.Id,
    mode: Settings.Mode.Id,
    groupingPattern: Option[Settings.GroupingPattern.Id]): Action[AnyContent] =
    Action.async { _ =>
      for {
        competitors <- service.getNewGroups(competitionId, mode, groupingPattern.map(_.id)).invoke
      } yield Ok(Json.toJson(competitors))
    }
}
