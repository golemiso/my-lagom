package com.golemiso.mylagom.player.impl

import akka.persistence.cassandra.session.scaladsl.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{ AggregateEventTag, ReadSideProcessor }
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraReadSide

import scala.concurrent.ExecutionContext

private[impl] class PlayerEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext) extends ReadSideProcessor[PlayerEvent] {
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PlayerEvent] = ???

  override def aggregateTags: Set[AggregateEventTag[PlayerEvent]] = ???
}
