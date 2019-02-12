package com.golemiso.mylagom.battle.impl

import akka.Done
import com.golemiso.mylagom.battle.api.Battle.Result
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

class ResultEntity extends PersistentEntity {
  override type Command = ResultCommand
  override type Event = ResultEvent
  override type State = Option[Result]
  override def initialState: State = None

  override def behavior: Behavior = {
    case Some(_) =>
      Actions().onReadOnlyCommand[ResultCommand.Read.type, Option[Result]] {
        case (ResultCommand.Read, ctx, state) => ctx.reply(state)
      }.onReadOnlyCommand[ResultCommand.Create, Done] {
        case (ResultCommand.Create(_), ctx, _) => ctx.invalidCommand("Result already exists")
      }.onCommand[ResultCommand.Update, Done] {
        case (ResultCommand.Update(result), ctx, _) =>
          ctx.thenPersist(ResultEvent.Updated(result))(_ => ctx.reply(Done))
      }.onCommand[ResultCommand.Delete.type, Done] {
        case (ResultCommand.Delete, ctx, _) =>
          ctx.thenPersist(ResultEvent.Deleted)(_ => ctx.reply(Done))
      }.onEvent {
        case (ResultEvent.Updated(result), _) => Some(result)
        case (ResultEvent.Deleted, _) => None
      }
    case None =>
      Actions().onReadOnlyCommand[ResultCommand.Read.type, Option[Result]] {
        case (ResultCommand.Read, ctx, state) => ctx.reply(state)
      }.onCommand[ResultCommand.Create, Done] {
        case (ResultCommand.Create(result), ctx, _) =>
          ctx.thenPersist(ResultEvent.Created(result))(_ => ctx.reply(Done))
      }.onCommand[ResultCommand.Update, Done] {
        case (ResultCommand.Update(result), ctx, _) =>
          ctx.thenPersist(ResultEvent.Updated(result))(_ => ctx.reply(Done))
      }.onEvent {
        case (ResultEvent.Created(result), _) => Some(result)
        case (ResultEvent.Updated(result), _) => Some(result)
      }
  }
}

sealed trait ResultCommand
object ResultCommand {
  case class Create(result: Result) extends ResultCommand with ReplyType[Done]
  case object Read extends ResultCommand with ReplyType[Option[Result]]
  case class Update(result: Result) extends ResultCommand with ReplyType[Done]
  case object Delete extends ResultCommand with ReplyType[Done]
}

sealed trait ResultEvent
object ResultEvent {
  case class Created(result: Result) extends ResultEvent
  case class Updated(result: Result) extends ResultEvent
  case object Deleted extends ResultEvent
}

object ResultSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[Result]
  )
}
