package controllers

import com.golemiso.mylagom.battle.api.{ TeamBattleRequest, BattleService }
import com.golemiso.mylagom.model.Battle
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class BattleController(mcc: MessagesControllerComponents, service: BattleService)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {}
