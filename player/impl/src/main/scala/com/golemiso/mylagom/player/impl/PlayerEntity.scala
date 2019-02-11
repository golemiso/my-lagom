package com.golemiso.mylagom.player.impl

import akka.Done
import com.golemiso.mylagom.player.api.Player
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

class PlayerEntity extends PersistentEntity {
  override type Command = PlayerCommand
  override type Event = PlayerEvent
  override type State = Option[Player]
  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(_) =>
      Actions().onReadOnlyCommand[PlayerCommand.Read.type, Option[Player]] {
        case (PlayerCommand.Read, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[PlayerCommand.Create, Done] {
        case (PlayerCommand.Create(_), ctx, _) => ctx.invalidCommand("Player already exists")
      }.onCommand[PlayerCommand.Update, Done] {
        case (PlayerCommand.Update(player), ctx, _) =>
          ctx.thenPersist(PlayerEvent.Updated(player))(_ => ctx.reply(Done))
      }.onCommand[PlayerCommand.Delete.type, Done] {
        case (PlayerCommand.Delete, ctx, _) =>
          ctx.thenPersist(PlayerEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (PlayerEvent.Updated(player), _) => Some(player)
        case (PlayerEvent.Deleted, _) => None
      }
    case None =>
      Actions().onReadOnlyCommand[PlayerCommand.Read.type, Option[Player]] {
        case (PlayerCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[PlayerCommand.Create, Done] {
        case (PlayerCommand.Create(player), ctx, _) =>
          ctx.thenPersist(PlayerEvent.Created(player))(_ => ctx.reply(Done))
      }.onCommand[PlayerCommand.Update, Done] {
        case (PlayerCommand.Update(player), ctx, _) =>
          ctx.thenPersist(PlayerEvent.Updated(player))(_ => ctx.reply(Done))
      }.onEvent {
        case (PlayerEvent.Created(player), _) => Some(player)
        case (PlayerEvent.Updated(player), _) => Some(player)
      }
  }
}

sealed trait PlayerCommand
object PlayerCommand {
  case class Create(player: Player) extends PlayerCommand with ReplyType[Done]
  case object Read extends PlayerCommand with ReplyType[Option[Player]]
  case class Update(player: Player) extends PlayerCommand with ReplyType[Done]
  case object Delete extends PlayerCommand with ReplyType[Done]
}

sealed trait PlayerEvent
object PlayerEvent {
  case class Created(player: Player) extends PlayerEvent
  case class Updated(player: Player) extends PlayerEvent
  case object Deleted extends PlayerEvent
}

object PlayerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Player]
  )
}
