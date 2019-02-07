package com.golemiso.mylagom.competition.impl

import akka.Done
import com.golemiso.mylagom.competition.api.Competition
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

class CompetitionEntity extends PersistentEntity {
  override type Command = CompetitionCommand
  override type Event = CompetitionEvent
  override type State = Option[Competition]

  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(competition) =>
      Actions()
    case None =>
      Actions().onCommand[CompetitionCommand.Create, Done] {
        case (CompetitionCommand.Create(competition), context, _) =>
          context.thenPersist(CompetitionEvent.Created(competition))(_ => context.reply(Done))
      }.onEvent {
        case (CompetitionEvent.Created(competition), _) => Some(competition)
      }
  }
}

sealed trait CompetitionCommand
object CompetitionCommand {
  case class Create(competition: Competition) extends CompetitionCommand with ReplyType[Done]
}

sealed trait CompetitionEvent
object CompetitionEvent {
  case class Created(competition: Competition) extends CompetitionEvent
}

object CompetitionSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Competition]
  )
}
