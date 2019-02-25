package com.golemiso.mylagom.team.impl

import akka.Done
import com.golemiso.mylagom.model.Team
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{ JsonSerializer, JsonSerializerRegistry }
import play.api.libs.json.{ Format, Json }

class TeamEntity extends PersistentEntity {
  override type Command = TeamCommand
  override type Event = TeamEvent
  override type State = Option[Team]

  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(_) =>
      Actions()
        .onReadOnlyCommand[TeamCommand.Read.type, Option[Team]] {
          case (TeamCommand.Read, ctx, state) => ctx.reply(state)
        }.onReadOnlyCommand[TeamCommand.Create, Done] {
          case (TeamCommand.Create(_), ctx, _) => ctx.invalidCommand("Team already exists")
        }.onCommand[TeamCommand.Update, Done] {
          case (TeamCommand.Update(team), ctx, _) =>
            ctx.thenPersist(TeamEvent.Updated(team))(_ => ctx.reply(Done))
        }.onCommand[TeamCommand.Delete.type, Done] {
          case (TeamCommand.Delete, ctx, _) =>
            ctx.thenPersist(TeamEvent.Deleted)(_ => ctx.reply(Done))
        }.onEvent {
          case (TeamEvent.Updated(team), _) => Some(team)
          case (TeamEvent.Deleted, _)       => None
        }
    case None =>
      Actions()
        .onReadOnlyCommand[TeamCommand.Read.type, Option[Team]] {
          case (TeamCommand.Read, ctx, state) => ctx.reply(state)
        }.onCommand[TeamCommand.Create, Done] {
          case (TeamCommand.Create(team), ctx, _) =>
            ctx.thenPersist(TeamEvent.Created(team))(_ => ctx.reply(Done))
        }.onCommand[TeamCommand.Update, Done] {
          case (TeamCommand.Update(team), ctx, _) =>
            ctx.thenPersist(TeamEvent.Updated(team))(_ => ctx.reply(Done))
        }.onEvent {
          case (TeamEvent.Created(team), _) => Some(team)
          case (TeamEvent.Updated(team), _) => Some(team)
        }
  }
}

sealed trait TeamCommand
object TeamCommand {
  case class Create(team: Team) extends TeamCommand with ReplyType[Done]
  case object Read extends TeamCommand with ReplyType[Option[Team]]
  case class Update(team: Team) extends TeamCommand with ReplyType[Done]
  case object Delete extends TeamCommand with ReplyType[Done]
}

sealed trait TeamEvent
object TeamEvent {
  case class Created(team: Team) extends TeamEvent
  object Created {
    implicit val format: Format[Created] = Json.format
  }

  case class Updated(team: Team) extends TeamEvent
  object Updated {
    implicit val format: Format[Updated] = Json.format
  }

  case object Deleted extends TeamEvent {
    implicit val format: Format[Deleted.type] = JsonSerializer.emptySingletonFormat(Deleted)
  }
}

object TeamSerializerRegistry extends JsonSerializerRegistry {
  override def serializers =
    List(
      JsonSerializer[Team],
      JsonSerializer[TeamEvent.Created],
      JsonSerializer[TeamEvent.Updated],
      JsonSerializer[TeamEvent.Deleted.type])
}
