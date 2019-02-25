package controllers

import com.golemiso.mylagom.aggregation.api.AggregationService
import com.golemiso.mylagom.model.Competition
import play.api.libs.json.{ Json, OFormat }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class RankingController(mcc: MessagesControllerComponents, aggregationService: AggregationService)(
  implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def get(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    aggregationService.rankingsBy(competitionId).invoke.map { playerRanking =>
      Ok(Json.toJson(playerRanking))
    }
  }
}

case class RankingResource(rank: Int, player: PlayerResource)
object RankingResource {
  implicit val format: OFormat[RankingResource] = Json.format[RankingResource]
}
