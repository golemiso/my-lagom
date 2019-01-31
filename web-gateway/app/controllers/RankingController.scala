package controllers

import play.api.libs.json.{ Json, OFormat }
import play.api.mvc._
import service.PlayerRecordService

import scala.concurrent.ExecutionContext

class RankingController(mcc: MessagesControllerComponents, playerRecordService: PlayerRecordService)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {

  def get(rankBy: String): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    playerRecordService.getAll.map { playerRecords =>
      val resource = playerRecords.map { playerRecord =>
        val rank = playerRecords.count(_.record.victory > playerRecord.record.victory) + 1
        RankingResource(rank, playerRecord.player)
      }.sortBy(_.rank)
      Ok(Json.toJson(resource))
    }
  }
}

case class RankingResource(rank: Int, player: PlayerResource)
object RankingResource {
  implicit val format: OFormat[RankingResource] = Json.format[RankingResource]
}
