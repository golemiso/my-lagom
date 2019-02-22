package com.golemiso.mylagom.aggregation.impl

import akka.NotUsed
import akka.stream.Materializer
import com.golemiso.mylagom.aggregation.api.{ AggregationService, PlayerRanking }
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.model.{ Battle, Competition, Player, Team }
import com.golemiso.mylagom.player.api.PlayerService
import com.golemiso.mylagom.team.api.TeamService
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext

class AggregationServiceImpl(
  playerService: PlayerService,
  teamService: TeamService,
  battleService: BattleService,
  competitionService: CompetitionService)(implicit ec: ExecutionContext, mat: Materializer) extends AggregationService {
  override def rankingsBy(id: Competition.Id): ServiceCall[NotUsed, Seq[PlayerRanking]] = ServiceCall { _ =>

    case class PlayerRecord(player: Player, record: PlayerRecord.Record) {
      def winningPercentage: Int = {
        if (record.victory > 0) {
          record.victory / (record.victory + record.defeat) * 100
        } else 0
      }
    }
    object PlayerRecord {
      def victory(player: Player) = PlayerRecord(player, Record(1, 0))
      def defeat(player: Player) = PlayerRecord(player, Record(0, 1))
      case class Record(victory: Int, defeat: Int)
    }

    for {
      competition <- competitionService.read(id).invoke
      players <- playerService.readAll.invoke
      teams <- teamService.readAll.invoke
      battles <- battleService.readAll.invoke
    } yield {
      val participants = players.filter(p => competition.participants.contains(p.id))
      val playerRecords = {
        battles.flatMap {
          case Battle(_, competitionId, _, competitors, Some(result)) if competitionId == id =>
            val leftPlayers = teams.filter(_.id == competitors.left).flatMap(t => players.filter(p => t.players.contains(p.id)))
            val rightPlayers = teams.filter(_.id == competitors.left).flatMap(t => players.filter(p => t.players.contains(p.id)))

            result match {
              case Battle.Result.VictoryLeft =>
                leftPlayers.map(PlayerRecord.victory) ++ rightPlayers.map(PlayerRecord.defeat)
              case Battle.Result.VictoryRight =>
                leftPlayers.map(PlayerRecord.defeat) ++ rightPlayers.map(PlayerRecord.victory)
              case _ => Nil
            }
          case _ => Nil
        } ++ participants.map(PlayerRecord.apply(_, PlayerRecord.Record(0, 0)))
      }.groupBy(_.player).toSeq.map {
        case (p, pr) => PlayerRecord(
          player = p,
          record = pr.map(_.record).foldLeft(PlayerRecord.Record(0, 0))((a, b) =>
            PlayerRecord.Record(victory = a.victory + b.victory, defeat = a.defeat + b.defeat)))
      }

      playerRecords.map { playerRecord =>
        val rank = playerRecords.count(_.winningPercentage > playerRecord.winningPercentage) + 1
        PlayerRanking(rank, playerRecord.player)
      }.sortBy(_.rank)
    }
  }

  override def competitionDetails(id: Competition.Id): ServiceCall[NotUsed, Competition] = ???

  override def battleDetails(id: Battle.Id): ServiceCall[NotUsed, Competition] = ???

  override def teamDetails(id: Team.Id): ServiceCall[NotUsed, Competition] = ???

  override def playerDetails(id: Player.Id): ServiceCall[NotUsed, Competition] = ???
}
