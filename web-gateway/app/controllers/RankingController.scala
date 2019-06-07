package controllers

import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.model.{ Competition, Player, PlayerRanking, PlayerRankings, Settings }
import com.golemiso.mylagom.player.api.PlayerService
import play.api.libs.json.{ Format, Json }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class RankingController(mcc: MessagesControllerComponents, battleService: BattleService, playerService: PlayerService)(
  implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def get(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    for {
      rankings <- battleService.readRankings(competitionId).invoke
      players <- playerService.readAll.invoke
      modes <- battleService.readModes(competitionId).invoke
    } yield {
      Ok(Json.toJson(PlayerRankingsResource(rankings, players, modes)))
    }
  }
}

case class PlayerRankingsResource(
  totalRanking: Seq[PlayerRankingResource],
  rankingsByMode: Seq[PlayerRankingsResource.RankingsByMode])
object PlayerRankingsResource {
  implicit val format: Format[PlayerRankingsResource] = Json.format

  case class RankingsByMode(mode: Settings.Mode, rankings: Seq[PlayerRankingResource])
  object RankingsByMode {
    implicit val format: Format[RankingsByMode] = Json.format
  }

  def apply(playerRankings: PlayerRankings, players: Seq[Player], modes: Seq[Settings.Mode]): PlayerRankingsResource = {
    PlayerRankingsResource(
      totalRanking = PlayerRankingResource(playerRankings.totalRanking, players),
      rankingsByMode = playerRankings.rankingsByMode.map { pr =>
        RankingsByMode(modes.find(_.id == pr.mode).get, PlayerRankingResource(pr.rankings, players))

      }
    )
  }
}

case class PlayerRankingResource(player: Player, ranking: PlayerRanking.Ranking)
object PlayerRankingResource {
  implicit val format: Format[PlayerRankingResource] = Json.format

  case class Ranking(ranking: Int) extends AnyVal
  object Ranking {
    implicit val format: Format[Ranking] = Json.valueFormat
  }

  def apply(playerRankings: Seq[PlayerRanking], players: Seq[Player]): Seq[PlayerRankingResource] = {
    playerRankings.map(ur => PlayerRankingResource(players.find(_.id == ur.player).get, ur.ranking))
  }
}
