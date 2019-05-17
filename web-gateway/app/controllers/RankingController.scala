package controllers

import play.api.mvc._

import scala.concurrent.ExecutionContext

class RankingController(mcc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

//  def get(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
//    aggregationService.rankingsBy(competitionId).invoke.map { playerRanking =>
//      Ok(Json.toJson(playerRanking))
//    }
//  }
}
