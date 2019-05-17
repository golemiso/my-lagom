package loader

import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.player.api.PlayerService
import com.lightbend.lagom.scaladsl.api.{ LagomConfigComponent, ServiceAcl, ServiceInfo }
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.softwaremill.macwire._
import controllers._
import play.api.ApplicationLoader.Context
import play.api.http.FileMimeTypes
import play.api.i18n._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.api.{ Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator }
import play.filters.HttpFiltersComponents
import play.filters.cors.{ CORSConfig, CORSFilter }
import router.Routes

import scala.collection.immutable
import scala.concurrent.ExecutionContext

abstract class WebGateway(context: Context)
  extends BuiltInComponentsFromContext(context)
  with AssetsComponents
  with I18nComponents
  with HttpFiltersComponents
  with AhcWSComponents
  with LagomConfigComponent
  with LagomServiceClientComponents {

  override lazy val serviceInfo: ServiceInfo =
    ServiceInfo("web-gateway", Map("web-gateway" -> immutable.Seq(ServiceAcl.forPathRegex("(?!/api/).*"))))

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(
    new CORSFilter(CORSConfig.fromConfiguration(configuration), httpErrorHandler))

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val playerService: PlayerService = serviceClient.implement[PlayerService]
  lazy val battleService: BattleService = serviceClient.implement[BattleService]
  lazy val competitionService: CompetitionService = serviceClient.implement[CompetitionService]

  lazy val messagesActionBuilder: MessagesActionBuilder = wire[WebGatewayMessagesActionBuilder]
  lazy val messagesControllerComponents: MessagesControllerComponents = wire[WebGatewayMessagesControllerComponents]

  lazy val playerController: PlayerController = wire[PlayerController]

  lazy val battleController: BattleController = wire[BattleController]

  lazy val rankingController: RankingController = wire[RankingController]

  lazy val competitionController: CompetitionController = wire[CompetitionController]

  lazy val settingController: SettingController = wire[SettingController]

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}

case class WebGatewayMessagesControllerComponents(
  messagesActionBuilder: MessagesActionBuilder,
  actionBuilder: DefaultActionBuilder,
  parsers: PlayBodyParsers,
  messagesApi: MessagesApi,
  langs: Langs,
  fileMimeTypes: FileMimeTypes,
  executionContext: scala.concurrent.ExecutionContext)
  extends MessagesControllerComponents

class WebGatewayMessagesActionBuilder(parser: BodyParser[AnyContent], messagesApi: MessagesApi)(
  implicit ec: ExecutionContext)
  extends MessagesActionBuilderImpl(parser, messagesApi)
  with MessagesActionBuilder {
  def this(parser: BodyParsers.Default, messagesApi: MessagesApi)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent], messagesApi)
  }
}

class WebGatewayLoader extends ApplicationLoader {
  def load(context: Context): Application = (new WebGateway(context) with LagomDevModeComponents).application
}
