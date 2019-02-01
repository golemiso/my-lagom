package com.golemiso.mylagom.player.impl

import akka.Done
import com.golemiso.mylagom.player.api.PlayerName
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}
import com.golemiso.mylagom.utils.JsonFormats._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

class PlayerEntity extends PersistentEntity {
  override type Command = PlayerCommand
  override type Event = PlayerEvent
  override type State = Option[Player]
  override def initialState = None

  override def behavior: Behavior = {
    case Some(player) =>
      Actions().onReadOnlyCommand[GetPlayer.type, Option[Player]] {
        case (GetPlayer, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[CreatePlayer, Done] {
        case (CreatePlayer(name), ctx, state) => ctx.invalidCommand("Player already exists")
      }
    case None =>
      Actions().onReadOnlyCommand[GetPlayer.type, Option[Player]] {
        case (GetPlayer, ctx, state) => ctx.reply(state)
      }.onCommand[CreatePlayer, Done] {
        case (CreatePlayer(name), ctx, state) =>
          ctx.thenPersist(PlayerCreated(name))(_ => ctx.reply(Done))
      }.onEvent {
        case (PlayerCreated(name), state) => Some(Player(name))
      }
  }
}

case class Player(name: PlayerName)

object Player {
  implicit val format: Format[Player] = Json.format
}

sealed trait PlayerEvent

case class PlayerCreated(name: PlayerName) extends PlayerEvent

object PlayerCreated {
  implicit val format: Format[PlayerCreated] = Json.format
}

sealed trait PlayerCommand

case class CreatePlayer(name: PlayerName) extends PlayerCommand with ReplyType[Done]

object CreatePlayer {
  implicit val format: Format[CreatePlayer] = Json.format
}

case object GetPlayer extends PlayerCommand with ReplyType[Option[Player]] {
  implicit val format: Format[GetPlayer.type] = singletonFormat(GetPlayer)
}

object PlayerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Player],
    JsonSerializer[PlayerCreated],
    JsonSerializer[CreatePlayer],
    JsonSerializer[GetPlayer.type]
  )
}
