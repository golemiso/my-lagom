package com.golemiso.mylagom.battle.impl

import akka.Done
import com.golemiso.mylagom.model.Battle
import com.lightbend.lagom.scaladsl.persistence.{ AggregateEvent, AggregateEventTag, PersistentEntity, PersistentEntityRegistry }
import com.lightbend.lagom.scaladsl.playjson.{ JsonSerializer, JsonSerializerRegistry }
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

class BattleEntity(registry: PersistentEntityRegistry) extends PersistentEntity {
  override type Command = BattleCommand
  override type Event = BattleEvent
  override type State = Option[Battle]
  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(_) =>
      Actions().onReadOnlyCommand[BattleCommand.Read.type, Option[Battle]] {
        case (BattleCommand.Read, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[BattleCommand.Create, Done] {
        case (BattleCommand.Create(_), ctx, _) => ctx.invalidCommand("Battle already exists")
      }.onCommand[BattleCommand.UpdateResult, Done] {
        case (BattleCommand.UpdateResult(result), ctx, Some(battle)) =>
          ctx.thenPersist(BattleEvent.ResultUpdated(battle(result)))(_ => ctx.reply(Done))
      }.onCommand[BattleCommand.Delete.type, Done] {
        case (BattleCommand.Delete, ctx, _) =>
          ctx.thenPersist(BattleEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleEvent.Deleted, _) => None
      }
    case None =>
      Actions().onReadOnlyCommand[BattleCommand.Read.type, Option[Battle]] {
        case (BattleCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[BattleCommand.Create, Done] {
        case (BattleCommand.Create(battle), ctx, _) =>
          ctx.thenPersist(BattleEvent.Created(battle))(_ => ctx.reply(Done))
      }.onEvent {
        case (BattleEvent.Created(battle), _) => Some(battle)
      }
  }
}

sealed trait BattleCommand
object BattleCommand {
  case class Create(battle: Battle) extends BattleCommand with ReplyType[Done]
  case object Read extends BattleCommand with ReplyType[Option[Battle]]
  case object Delete extends BattleCommand with ReplyType[Done]

  case class UpdateResult(result: Battle.Result) extends BattleCommand with ReplyType[Done]
}

sealed trait BattleEvent extends AggregateEvent[BattleEvent] {
  override def aggregateTag = BattleEvent.Tag
}
object BattleEvent {
  val Tag: AggregateEventTag[BattleEvent] = AggregateEventTag[BattleEvent]

  case class Created(battle: Battle) extends BattleEvent
  case object Deleted extends BattleEvent

  case class ResultUpdated(battle: Battle) extends BattleEvent
}

object BattleSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Battle])
}
