package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.Done
import com.golemiso.mylagom.battle.api.TeamBattleResultRequest.PlayersResultPair
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

class BattleResultsEntity(registry: PersistentEntityRegistry) extends PersistentEntity {
  override type Command = BattleResultsCommand
  override type Event = BattleResultsEvent
  override type State = BattleResultsStatus
  override def initialState: State = BattleResultsStatus.NotConfigured

  override def behavior: Behavior = {
    case BattleResultsStatus.NotConfigured                    => notConfigured
    case _: BattleResultsStatus.GroupBattleResultsStatus      => groupBattle
    case _: BattleResultsStatus.TeamBattleResultsStatus       => teamBattle
    case _: BattleResultsStatus.IndividualBattleResultsStatus => individualBattle
    case _ =>
      Actions()
        .onReadOnlyCommand[BattleResultsCommand.Read.type, BattleResultsStatus] {
          case (BattleResultsCommand.Read, ctx, state) => ctx.reply(state)
        }.onReadOnlyCommand[BattleResultsCommand.Create, Done] {
          case (BattleResultsCommand.Create(_), ctx, _) => ctx.invalidCommand("Battle already exists")
        }.onCommand[BattleResultsCommand.UpdateResults, Done] {
          case (BattleResultsCommand.UpdateResults(result), ctx, BattleHistoryStatus(battles)) =>
            ctx.thenPersist(BattleResultsEvent.ResultUpdated(battle(result)))(_ => ctx.reply(Done))
        }.onCommand[BattleResultsCommand.Delete.type, Done] {
          case (BattleResultsCommand.Delete, ctx, _) =>
            ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
        }.onEvent {
          case (BattleResultsEvent.ResultUpdated(battle), _) => Some(battle)
          case (BattleResultsEvent.Deleted, _)               => BattleResultsStatus(Nil)
        }
  }

  private val notConfigured = {
    Actions()
      .onCommand[BattleResultsCommand.Create, Done] {
        case (BattleResultsCommand.Create(style), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Created(style))(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleResultsEvent.Created(style), _) =>
          style match {
            case Competition.BattleStyle.Group =>
              BattleResultsStatus.GroupBattleResultsStatus(Nil, Nil, Nil)
            case Competition.BattleStyle.Team =>
              BattleResultsStatus.TeamBattleResultsStatus(Nil, Nil, Nil)
            case Competition.BattleStyle.Individual =>
              BattleResultsStatus.IndividualBattleResultsStatus(Nil, Nil, Nil)
          }
      }
  }

  private val groupBattle = {
    Actions()
      .onReadOnlyCommand[BattleResultsCommand.Read.type, BattleResultsStatus] {
        case (BattleResultsCommand.Read, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[BattleResultsCommand.Create, Done] {
        case (BattleResultsCommand.Create(_), ctx, _) => ctx.invalidCommand("Battle already exists")
      }.onCommand[BattleResultsCommand.UpdateResults, Done] {
        case (BattleResultsCommand.UpdateResults(battle, playersResultPairs), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ResultUpdated(battle, playersResultPairs))(_ => ctx.reply(Done))
      }.onCommand[BattleResultsCommand.Delete.type, Done] {
        case (BattleResultsCommand.Delete, ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleResultsEvent.ResultUpdated(battle, playersResultPairs), state: BattleResultsStatus.GroupBattleResultsStatus) =>
          state.battles.find(_.id == battle).map { b =>
            b.competitors.
          }
        case (BattleResultsEvent.Deleted, _)               => BattleResultsStatus.NotConfigured
      }
  }

  private val teamBattle = {
    Actions()
      .onReadOnlyCommand[BattleResultsCommand.Read.type, BattleResultsStatus] {
        case (BattleResultsCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[BattleResultsCommand.Delete.type, Done] {
        case (BattleResultsCommand.Delete, ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleResultsEvent.Deleted, _) => BattleResultsStatus.NotConfigured
      }
  }

  private val individualBattle = {
    Actions()
      .onReadOnlyCommand[BattleResultsCommand.Read.type, BattleResultsStatus] {
        case (BattleResultsCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[BattleResultsCommand.Delete.type, Done] {
        case (BattleResultsCommand.Delete, ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleResultsEvent.Deleted, _) => BattleResultsStatus.NotConfigured
      }
  }

  def id = Battle.Id(UUID.fromString(entityId))
}

sealed trait BattleResultsCommand
object BattleResultsCommand {
  case class Create(style: Competition.BattleStyle) extends BattleResultsCommand with ReplyType[Done]
  case class Add(battle: Battle) extends BattleResultsCommand with ReplyType[Done]
  case object Read extends BattleResultsCommand with ReplyType[Seq[Battle]]
  case object Delete extends BattleResultsCommand with ReplyType[Done]

  case class UpdateResults(battle: Battle.Id, playersResultPairs: Seq[PlayersResultPair])
    extends BattleResultsCommand
    with ReplyType[Done]
}

sealed trait BattleResultsEvent extends AggregateEvent[BattleResultsEvent] {
  override def aggregateTag: AggregateEventTag[BattleResultsEvent] = BattleResultsEvent.Tag
}
object BattleResultsEvent {
  val Tag: AggregateEventTag[BattleResultsEvent] = AggregateEventTag[BattleResultsEvent]

  case class Created(style: Competition.BattleStyle) extends BattleResultsEvent
  object Created {
    implicit val format: Format[Created] = Json.format
  }

  case class Added(battle: Battle) extends BattleResultsEvent
  object Added {
    implicit val format: Format[Added] = Json.format
  }

  case object Deleted extends BattleResultsEvent {
    implicit val format: Format[Deleted.type] = JsonSerializer.emptySingletonFormat(Deleted)
  }

  case class ResultUpdated(battle: Battle.Id, playersResultPairs: Seq[PlayersResultPair]) extends BattleResultsEvent
  object ResultUpdated {
    implicit val format: Format[ResultUpdated] = Json.format
  }
}

sealed trait BattleResultsStatus
object BattleResultsStatus {
  case class GroupBattleResultsStatus(
    resultConfigurations: Seq[Result],
    participants: Seq[Player.Id],
    battles: Seq[GroupBattle])
    extends BattleResultsStatus
  case class TeamBattleResultsStatus(
    resultConfigurations: Seq[Result],
    participants: Seq[Team.Id],
    battles: Seq[TeamBattle])
    extends BattleResultsStatus
  case class IndividualBattleResultsStatus(
    resultConfigurations: Seq[Result],
    participants: Seq[Player.Id],
    battles: Seq[IndividualBattle])
    extends BattleResultsStatus
  case object NotConfigured extends BattleResultsStatus
}

object BattleSerializerRegistry extends JsonSerializerRegistry {
  override def serializers =
    List(
      JsonSerializer[Battle],
      JsonSerializer[BattleResultsEvent.Created],
      JsonSerializer[BattleResultsEvent.Deleted.type],
      JsonSerializer[BattleResultsEvent.ResultUpdated]
    )
}
