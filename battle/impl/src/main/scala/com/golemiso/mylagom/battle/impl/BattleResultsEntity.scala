package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.Done
import com.golemiso.mylagom.battle.api.BattleResultsRequest
import com.golemiso.mylagom.model._
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity,
  PersistentEntityRegistry
}
import com.lightbend.lagom.scaladsl.playjson.{ JsonSerializer, JsonSerializerRegistry }
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{ Format, Json }

import scala.util.Random

class BattleResultsEntity(registry: PersistentEntityRegistry) extends PersistentEntity {
  override type Command = BattleResultsCommand
  override type Event = BattleResultsEvent
  override type State = BattleResultsStatus
  override def initialState: State = BattleResultsStatus()

  override def behavior: Behavior = { _ =>
    Actions()
      .onReadOnlyCommand[BattleResultsCommand.ReadBattles.type, Seq[Battle]] {
        case (BattleResultsCommand.ReadBattles, ctx, state) =>
          ctx.reply(state.battles)
      }.onReadOnlyCommand[BattleResultsCommand.GetNewGroups, Seq[Seq[Player.Id]]] {
        case (BattleResultsCommand.GetNewGroups(mode, rankBy), ctx, state) =>
          ctx.reply(newGroups(state, mode, rankBy))
      }.onCommand[BattleResultsCommand.AddBattle, Battle.Id] {
        case (BattleResultsCommand.AddBattle(battle), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.BattleAdded(battle))(_ => ctx.reply(battle.id))
      }.onCommand[BattleResultsCommand.UpdateResults, Done] {
        case (BattleResultsCommand.UpdateResults(battle, results), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ResultUpdated(battle, results))(_ => ctx.reply(Done))
      }.onCommand[BattleResultsCommand.DeleteBattle.type, Done] {
        case (BattleResultsCommand.DeleteBattle, ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
      }.onCommand[BattleResultsCommand.AddMode, Settings.Mode.Id] {
        case (BattleResultsCommand.AddMode(mode), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ModeAdded(mode))(_ => ctx.reply(mode.id))
      }.onCommand[BattleResultsCommand.AddParticipant, Done] {
        case (BattleResultsCommand.AddParticipant(participant), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ParticipantAdded(participant))(_ => ctx.reply(Done))
      }.onCommand[BattleResultsCommand.AddGroupingPattern, Settings.GroupingPattern.Id] {
        case (BattleResultsCommand.AddGroupingPattern(groupingPattern), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.GroupingPatternAdded(groupingPattern))(_ => ctx.reply(groupingPattern.id))
      }.onCommand[BattleResultsCommand.AddResult, Settings.Result.Id] {
        case (BattleResultsCommand.AddResult(result), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ResultAdded(result))(_ => ctx.reply(result.id))
      }.onEvent {
        case (BattleResultsEvent.BattleAdded(battle), state) =>
          state.copy(battles = state.battles :+ battle)
        case (BattleResultsEvent.BattleUpdated(battle), state) =>
          state.updateBattle(battle)
        case (BattleResultsEvent.ModeAdded(mode), state) =>
          state.copy(settings = state.settings.copy(modes = state.settings.modes :+ mode))
        case (BattleResultsEvent.ParticipantAdded(participant), state) =>
          state.copy(settings = state.settings.copy(participants = state.settings.participants :+ participant))
        case (BattleResultsEvent.GroupingPatternAdded(groupingPattern), state) =>
          state.copy(
            settings = state.settings.copy(groupingPatterns = state.settings.groupingPatterns :+ groupingPattern))
        case (BattleResultsEvent.ResultAdded(result), state) =>
          state.copy(settings = state.settings.copy(results = state.settings.results :+ result))
      }
  }

  private def id = Competition.Id(UUID.fromString(entityId))

  private def newGroups(state: State, mode: Settings.Mode.Id, rankBy: Settings.GroupingPattern.RankBy) = {

    val allRankings = createRankings(state)
    val groupingPattern = Random.shuffle(state.settings.groupingPatterns).head

    val baseRanking = rankBy match {
      case Settings.GroupingPattern.RankBy.AllResult =>
        allRankings.totalRanking
      case Settings.GroupingPattern.RankBy.ModeResult =>
        allRankings.rankingsByMode.find(_.mode == mode).map(_.rankings).get
      case Settings.GroupingPattern.RankBy.Unknown =>
        throw new Exception
    }

    if (baseRanking.length != groupingPattern.groups.flatMap(_.MemberRankings).length) throw new Exception

    val rankingsWithoutTies =
      Random.shuffle(baseRanking).sorted(Ordering.by((_: PlayerRanking).ranking.ranking).reverse)

    groupingPattern.groups.map(_.MemberRankings.map(r => rankingsWithoutTies(r.ranking - 1).player))
  }

  private def createRankings(state: State) = {

    def ranking(tuple: Seq[(Player.Id, Int)]) = {
      tuple.map(t1 => PlayerRanking(t1._1, PlayerRanking.Ranking(tuple.count(t2 => t1._2 > t2._2) + 1)))
    }

    PlayerRankings(
      ranking(state.playerBattleResults.map(pbr => (pbr.id, pbr.score.totalScore.score))),
      state.settings.modes.map { m =>
        PlayerRankings.RankingsByMode(
          m.id,
          ranking(state.playerBattleResults.map(pbr =>
            (pbr.id, pbr.score.scoresByMode.find(_.mode == m.id).map(_.score.score).getOrElse(0)))))
      }
    )
  }
}

sealed trait BattleResultsCommand
object BattleResultsCommand {
  case class Create() extends BattleResultsCommand with ReplyType[Done]
  case class AddBattle(battle: Battle) extends BattleResultsCommand with ReplyType[Battle.Id]
  case object ReadBattles extends BattleResultsCommand with ReplyType[Seq[Battle]]
  case object DeleteBattle extends BattleResultsCommand with ReplyType[Done]

  case class UpdateResults(battle: Battle.Id, results: Seq[BattleResultsRequest.CompetitorResultPair])
    extends BattleResultsCommand
    with ReplyType[Done]

  case class AddMode(mode: Settings.Mode) extends BattleResultsCommand with ReplyType[Settings.Mode.Id]
  case class AddParticipant(participant: Player.Id) extends BattleResultsCommand with ReplyType[Done]
  case class AddGroupingPattern(groupingPattern: Settings.GroupingPattern)
    extends BattleResultsCommand
    with ReplyType[Settings.GroupingPattern.Id]
  case class AddResult(result: Settings.Result) extends BattleResultsCommand with ReplyType[Settings.Result.Id]

  case class GetNewGroups(mode: Settings.Mode.Id, rankBy: Settings.GroupingPattern.RankBy)
    extends BattleResultsCommand
    with ReplyType[Seq[Seq[Player.Id]]]
}

sealed trait BattleResultsEvent extends AggregateEvent[BattleResultsEvent] {
  override def aggregateTag: AggregateEventTag[BattleResultsEvent] = BattleResultsEvent.Tag
}
object BattleResultsEvent {
  val Tag: AggregateEventTag[BattleResultsEvent] = AggregateEventTag[BattleResultsEvent]

  case class BattleAdded(battle: Battle) extends BattleResultsEvent
  object BattleAdded {
    implicit val format: Format[BattleAdded] = Json.format
  }

  case object Deleted extends BattleResultsEvent {
    implicit val format: Format[Deleted.type] = JsonSerializer.emptySingletonFormat(Deleted)
  }

  case class BattleUpdated(battle: Battle) extends BattleResultsEvent
  object BattleUpdated {
    implicit val format: Format[BattleUpdated] = Json.format
  }

  case class ResultUpdated(battle: Battle.Id, results: Seq[BattleResultsRequest.CompetitorResultPair])
    extends BattleResultsEvent
  object ResultUpdated {
    implicit val format: Format[ResultUpdated] = Json.format
  }

  case class ModeAdded(mode: Settings.Mode) extends BattleResultsEvent
  object ModeAdded {
    implicit val format: Format[ModeAdded] = Json.format
  }

  case class ParticipantAdded(participant: Player.Id) extends BattleResultsEvent
  object ParticipantAdded {
    implicit val format: Format[ParticipantAdded] = Json.format
  }

  case class GroupingPatternAdded(groupingPattern: Settings.GroupingPattern) extends BattleResultsEvent
  object GroupingPatternAdded {
    implicit val format: Format[GroupingPatternAdded] = Json.format
  }

  case class ResultAdded(result: Settings.Result) extends BattleResultsEvent
  object ResultAdded {
    implicit val format: Format[ResultAdded] = Json.format
  }
}

case class BattleResultsStatus(
  battles: Seq[Battle] = Nil,
  settings: Settings = Settings(),
  playerBattleResults: Seq[PlayerBattleResults] = Nil) {
  def updateBattle(battle: Battle): BattleResultsStatus = {
    val updatedBattles = battles.map {
      case b if b.id == battle.id => battle
      case b                      => b
    }

    copy(battles = updatedBattles, playerBattleResults = createPlayerBattleResults(updatedBattles))
  }

  private def createPlayerBattleResults(updatedBattles: Seq[Battle]) = {
    settings.participants.map { playerId =>
      val playerBattles = battles.filter(_.competitors.exists(_.players.contains(playerId)))

      val scoresByMode = settings.modes.map { mode =>
        val modeResults = playerBattles
          .filter(_.mode == mode.id).flatMap(_.competitors.find(_.players.contains(playerId)).flatMap(_.result))

        val modePointSum = modeResults.flatMap { resultId =>
          settings.results.collect {
            case r if r.id == resultId => r.point.point
          }
        }.sum

        PlayerScore.ScoresByMode(mode.id, PlayerScore.Score(modePointSum))
      }

      PlayerBattleResults(playerId, playerBattles, PlayerScore(scoresByMode))
    }
  }
}
object BattleResultsStatus {
  implicit val format: Format[BattleResultsStatus] = Json.format
}

case class PlayerBattleResults(id: Player.Id, battles: Seq[Battle], score: PlayerScore)
object PlayerBattleResults {
  implicit val format: Format[PlayerBattleResults] = Json.format
}

case class PlayerScore(totalScore: PlayerScore.Score, scoresByMode: Seq[PlayerScore.ScoresByMode])
object PlayerScore {
  implicit val format: Format[PlayerScore] = Json.format

  def apply(scoresByMode: Seq[PlayerScore.ScoresByMode]): PlayerScore =
    PlayerScore(Score(scoresByMode.map(_.score.score).sum), scoresByMode)

  case class Score(score: Int) extends AnyVal
  object Score {
    implicit val format: Format[Score] = Json.valueFormat
  }

  case class ScoresByMode(mode: Settings.Mode.Id, score: Score)
  object ScoresByMode {
    implicit val format: Format[ScoresByMode] = Json.format
  }
}

case class PlayerRankings(totalRanking: Seq[PlayerRanking], rankingsByMode: Seq[PlayerRankings.RankingsByMode])
object PlayerRankings {
  implicit val format: Format[PlayerRankings] = Json.format

  case class RankingsByMode(mode: Settings.Mode.Id, rankings: Seq[PlayerRanking])
  object RankingsByMode {
    implicit val format: Format[RankingsByMode] = Json.format
  }
}

case class PlayerRanking(player: Player.Id, ranking: PlayerRanking.Ranking)
object PlayerRanking {
  implicit val format: Format[PlayerRanking] = Json.format

  case class Ranking(ranking: Int) extends AnyVal
  object Ranking {
    implicit val format: Format[Ranking] = Json.valueFormat
  }
}

object BattleSerializerRegistry extends JsonSerializerRegistry {
  override def serializers =
    List(
      JsonSerializer[BattleResultsStatus],
      JsonSerializer[BattleResultsEvent.Deleted.type],
      JsonSerializer[BattleResultsEvent.ResultUpdated]
    )
}
