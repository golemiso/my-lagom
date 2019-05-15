package controllers

import com.golemiso.mylagom.battle.api.{ BattleRequest, BattleService }
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model._
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class BattleController(
  mcc: MessagesControllerComponents,
  service: BattleService,
  competitionService: CompetitionService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  private def fetchCompetition(id: Competition.Id): Future[Competition] = competitionService.read(id).invoke

  def post(competitionId: Competition.Id): Action[BattleRequest] = Action.async(parse.json[BattleRequest]) { request =>
    for {
      _ <- fetchCompetition(competitionId)
      _ <- service.add(competitionId).invoke(request.body)
    } yield Ok
  }
}
