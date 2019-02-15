package com.golemiso.mylagom.competition.impl

import akka.stream.scaladsl.Flow
import com.golemiso.mylagom.battle.api.{BattleEvent, BattleService}
import com.golemiso.mylagom.model.Battle

class BrokerEventConsumer(battleService: BattleService) {

  private val battleEvents = battleService.events
  battleEvents.subscribe.withGroupId("competition-service")
    .atLeastOnce(Flow[BattleEvent]{
      case BattleEvent.Created(Battle(id, slug, name, mode, teams, result))
      case BattleEvent.ResultUpdated(Battle(id, slug, name, mode, teams, result)) =>
        Unit
      case _ =>
        Unit
    })
}
