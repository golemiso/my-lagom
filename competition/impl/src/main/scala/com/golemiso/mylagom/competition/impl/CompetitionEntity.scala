package com.golemiso.mylagom.competition.impl

import akka.Done
import com.golemiso.mylagom.model.{ Competition, Player }
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{ JsonSerializer, JsonSerializerRegistry }
import play.api.libs.json.{ Format, Json }

class CompetitionEntity extends PersistentEntity {
  override type Command = CompetitionCommand
  override type Event = CompetitionEvent
  override type State = Option[Competition]

  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(_) =>
      Actions()
        .onReadOnlyCommand[CompetitionCommand.Read.type, Option[Competition]] {
          case (CompetitionCommand.Read, ctx, state) => ctx.reply(state)
        }.onReadOnlyCommand[CompetitionCommand.Create, Done] {
          case (CompetitionCommand.Create(_), ctx, _) => ctx.invalidCommand("Competition already exists")
        }
    case None =>
      Actions()
        .onReadOnlyCommand[CompetitionCommand.Read.type, Option[Competition]] {
          case (CompetitionCommand.Read, ctx, state) => ctx.reply(state)
        }.onCommand[CompetitionCommand.Create, Done] {
          case (CompetitionCommand.Create(competition), ctx, _) =>
            ctx.thenPersist(CompetitionEvent.Created(competition))(_ => ctx.reply(Done))
        }.onEvent {
          case (CompetitionEvent.Created(competition), _) => Some(competition)
        }
  }
}

sealed trait CompetitionCommand
object CompetitionCommand {
  case class Create(competition: Competition) extends CompetitionCommand with ReplyType[Done]

  case object Read extends CompetitionCommand with ReplyType[Option[Competition]] {
    implicit val format: Format[Read.type] = JsonSerializer.emptySingletonFormat(Read)
  }
}

sealed trait CompetitionEvent
object CompetitionEvent {
  case class Created(competition: Competition) extends CompetitionEvent
  object Created {
    implicit val format: Format[Created] = Json.format
  }

  case class ParticipantAdded(competition: Competition) extends CompetitionEvent
  object ParticipantAdded {
    implicit val format: Format[ParticipantAdded] = Json.format
  }
}

object CompetitionSerializerRegistry extends JsonSerializerRegistry {
  override def serializers =
    List(
      JsonSerializer[Competition],
      JsonSerializer[CompetitionEvent.Created],
      JsonSerializer[CompetitionEvent.ParticipantAdded])
}
