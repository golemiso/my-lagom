package controllers

import com.golemiso.mylagom.battle.api._
import com.golemiso.mylagom.model.Battle.Competitor
import com.golemiso.mylagom.model._
import com.golemiso.mylagom.player.api.PlayerService
import play.api.libs.json.{ Format, Json }
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class BattleController(mcc: MessagesControllerComponents, service: BattleService, playerService: PlayerService)(
  implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def getAll(competitionId: Competition.Id): Action[AnyContent] = Action.async { _ =>
    for {
      bs <- service.readAllBattles(competitionId).invoke
      ms <- service.readModes(competitionId).invoke
      pl <- playerService.readAll.invoke
    } yield {
      Ok(Json.toJson(bs.reverse.map { b =>
        BattleResource(b.id, ms.find(_.id == b.mode).get, b.competitors.map { c =>
          CompetitorResource(c.id, c.players.map(p => pl.find(_.id == p).get), c.result)
        })
      }))
    }
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
    groupingPattern: Option[Settings.GroupingPattern.Id]): Action[AnyContent] = Action.async { _ =>
    val participants = service.readParticipants(competitionId).invoke
    val competitors = service.getNewGroups(competitionId, mode, groupingPattern.map(_.id)).invoke

    for {
      cs <- competitors
      pl <- playerService.readAll.invoke
    } yield {
      Ok(Json.toJson(cs.map { c =>
        CompetitorResource(c.id, c.players.map(p => pl.find(_.id == p).get), c.result)
      }))
    }
  }

  case class CompetitorResource(id: Competitor.Id, players: Seq[Player], result: Option[Settings.Result.Id])
  object CompetitorResource {
    implicit val format: Format[CompetitorResource] = Json.format
  }

  case class BattleResource(id: Battle.Id, mode: Settings.Mode, competitors: Seq[CompetitorResource])
  object BattleResource {
    implicit val format: Format[BattleResource] = Json.format
  }
}
