package com.golemiso.mylagom.battle.impl

import java.util.UUID

import akka.Done
import com.golemiso.mylagom.battle.api.BattleResultRequest
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
  override def initialState: State = BattleResultsStatus(Nil)

  override def behavior: Behavior = { _ =>
    Actions()
      .onReadOnlyCommand[BattleResultsCommand.Read.type, Seq[Battle]] {
        case (BattleResultsCommand.Read, ctx, BattleResultsStatus(battles)) =>
          ctx.reply(battles)
      }.onCommand[BattleResultsCommand.Add, Battle.Id] {
        case (BattleResultsCommand.Add(battle), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Added(battle))(_ => ctx.reply(battle.id))
      }.onCommand[BattleResultsCommand.UpdateResults, Done] {
        case (BattleResultsCommand.UpdateResults(battle, results), ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.ResultUpdated(battle, results))(_ => ctx.reply(Done))
      }.onCommand[BattleResultsCommand.Delete.type, Done] {
        case (BattleResultsCommand.Delete, ctx, _) =>
          ctx.thenPersist(BattleResultsEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleResultsEvent.BattleUpdated(battle), state) =>
          state.updateBattle(battle)
        case (BattleResultsEvent.Deleted, _) => initialState
      }
  }

  def id = Competition.Id(UUID.fromString(entityId))
}

sealed trait BattleResultsCommand
object BattleResultsCommand {
  case class Create() extends BattleResultsCommand with ReplyType[Done]
  case class Add(battle: Battle) extends BattleResultsCommand with ReplyType[Battle.Id]
  case object Read extends BattleResultsCommand with ReplyType[Seq[Battle]]
  case object Delete extends BattleResultsCommand with ReplyType[Done]

  case class UpdateResults(battle: Battle.Id, results: Seq[BattleResultRequest.CompetitorResultPair])
    extends BattleResultsCommand
    with ReplyType[Done]

  case class AddParticipant(participant: Player.Id) extends BattleResultsCommand with ReplyType[Done]
}

sealed trait BattleResultsEvent extends AggregateEvent[BattleResultsEvent] {
  override def aggregateTag: AggregateEventTag[BattleResultsEvent] = BattleResultsEvent.Tag
}
object BattleResultsEvent {
  val Tag: AggregateEventTag[BattleResultsEvent] = AggregateEventTag[BattleResultsEvent]

  case class Added(battle: Battle) extends BattleResultsEvent
  object Added {
    implicit val format: Format[Added] = Json.format
  }

  case object Deleted extends BattleResultsEvent {
    implicit val format: Format[Deleted.type] = JsonSerializer.emptySingletonFormat(Deleted)
  }

  case class BattleUpdated(battle: Battle) extends BattleResultsEvent
  object BattleUpdated {
    implicit val format: Format[BattleUpdated] = Json.format
  }

  case class ResultUpdated(battle: Battle.Id, results: Seq[BattleResultRequest.CompetitorResultPair])
    extends BattleResultsEvent
  object ResultUpdated {
    implicit val format: Format[ResultUpdated] = Json.format
  }
}

case class BattleResultsStatus(battles: Seq[Battle]) {
  def updateBattle(battle: Battle): BattleResultsStatus = {
    copy(battles = battles.map {
      case b if b.id == battle.id => battle
      case b                      => b
    })
  }
}

object BattleSerializerRegistry extends JsonSerializerRegistry {
  override def serializers =
    List(
      JsonSerializer[Battle],
      JsonSerializer[BattleResultsEvent.Deleted.type],
      JsonSerializer[BattleResultsEvent.ResultUpdated]
    )
}
