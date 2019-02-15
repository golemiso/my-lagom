package com.golemiso.mylagom.battle.api

import com.golemiso.mylagom.model.Battle
import play.api.libs.json._

sealed trait BattleEvent
object BattleEvent {
  implicit val format: Format[BattleEvent] = julienrf.json.derived.flat.oformat((__ \ "type").format[String])

  case class Created(battle: Battle) extends BattleEvent
  case object Deleted extends BattleEvent

  case class ResultUpdated(battle: Battle) extends BattleEvent
}
