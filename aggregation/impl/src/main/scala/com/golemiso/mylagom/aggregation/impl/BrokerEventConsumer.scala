package com.golemiso.mylagom.aggregation.impl

import akka.Done
import akka.stream.scaladsl.Flow
import com.golemiso.mylagom.battle.api.{ BattleEvent, BattleService }
import com.golemiso.mylagom.model.Battle
import com.lightbend.lagom.scaladsl.api.broker.Message

class BrokerEventConsumer(battleService: BattleService) {

  //  private val battleEvents = battleService.events
  //  battleEvents
  //    .subscribe.withMetadata
  //    .withGroupId("competition-service")
  //    .atLeastOnce(Flow[Message[BattleEvent]].map {
  //      case msg@BattleEvent.Created(Battle(id, slug, name, mode, teams, result)) =>
  //        msg.messageKeyAsString
  //        Done
  //      case msg@BattleEvent.ResultUpdated(Battle(id, slug, name, mode, teams, result)) =>
  //        Done
  //      case _ =>
  //        Done
  //    })
}
