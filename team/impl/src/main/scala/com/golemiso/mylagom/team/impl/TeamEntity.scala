package com.golemiso.mylagom.team.impl

import akka.Done
import com.golemiso.mylagom.team.api.Team
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.Format

class TeamEntity extends PersistentEntity {
  override type Command = TeamCommand
  override type Event = TeamEvent
  override type State = Option[Team]

  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(team) =>
      Actions().onReadOnlyCommand[TeamCommand.Read.type, Option[Team]] {
        case (TeamCommand.Read, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[TeamCommand.Create, Done] {
        case (TeamCommand.Create(_), ctx, _) => ctx.invalidCommand("Player already exists")
      }
    case None =>
      Actions().onReadOnlyCommand[TeamCommand.Read.type, Option[Team]] {
        case (TeamCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[TeamCommand.Create, Done] {
        case (TeamCommand.Create(team), context, _) =>
          context.thenPersist(TeamEvent.Created(team))(_ => context.reply(Done))
      }.onEvent {
        case (TeamEvent.Created(team), _) => Some(team)
      }
  }
}

sealed trait TeamCommand
object TeamCommand {
  case class Create(team: Team) extends TeamCommand with ReplyType[Done]

  case object Read extends TeamCommand with ReplyType[Option[Team]] {
    implicit val format: Format[Read.type] = JsonSerializer.emptySingletonFormat(Read)
  }
}

sealed trait TeamEvent
object TeamEvent {
  case class Created(team: Team) extends TeamEvent
}

object TeamSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Team]
  )
}
