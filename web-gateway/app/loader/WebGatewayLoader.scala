package loader

import com.golemiso.mylagom.aggregation.api.AggregationService
import com.golemiso.mylagom.battle.api.BattleService
import com.golemiso.mylagom.competition.api.CompetitionService
import com.golemiso.mylagom.team.api.TeamService
import com.golemiso.mylagom.player.api.PlayerService
import com.lightbend.lagom.scaladsl.api.{ LagomConfigComponent, ServiceAcl, ServiceInfo }
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.softwaremill.macwire._
import com.typesafe.config.Config
import controllers.{ AssetsComponents, BattleController, CompetitionController, PlayerController, RankingController, TeamController }
import domain.{ BattleRepository, PlayerRepository, TeamRepository }
import infra.mongodb.{ MongoDBBattleRepository, MongoDBPlayerRepository, MongoDBTeamRepository }
import play.api.ApplicationLoader.Context
import play.api.http.FileMimeTypes
import play.api.i18n._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.api.{ Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator }
import play.filters.HttpFiltersComponents
import play.filters.cors.{ CORSConfig, CORSFilter }
import reactivemongo.api.{ DefaultDB, MongoConnection, MongoDriver }
import router.Routes
import service.PlayerRecordService

import scala.collection.immutable
import scala.concurrent.{ ExecutionContext, Future }

abstract class WebGateway(context: Context) extends BuiltInComponentsFromContext(context)
  with DBComponents
  with AssetsComponents
  with I18nComponents
  with HttpFiltersComponents
  with AhcWSComponents
  with LagomConfigComponent
  with LagomServiceClientComponents {

  override lazy val serviceInfo: ServiceInfo = ServiceInfo(
    "web-gateway",
    Map(
      "web-gateway" -> immutable.Seq(ServiceAcl.forPathRegex("(?!/api/).*"))))

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(new CORSFilter(CORSConfig.fromConfiguration(configuration), httpErrorHandler))

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val playerService: PlayerService = serviceClient.implement[PlayerService]
  lazy val teamService: TeamService = serviceClient.implement[TeamService]
  lazy val battleService: BattleService = serviceClient.implement[BattleService]
  lazy val competitionService: CompetitionService = serviceClient.implement[CompetitionService]
  lazy val aggregationService: AggregationService = serviceClient.implement[AggregationService]

  lazy val messagesActionBuilder: MessagesActionBuilder = wire[WebGatewayMessagesActionBuilder]
  lazy val messagesControllerComponents: MessagesControllerComponents = wire[WebGatewayMessagesControllerComponents]

  lazy val playerController: PlayerController = wire[PlayerController]
  lazy val playerRepository: PlayerRepository = wire[MongoDBPlayerRepository]

  lazy val teamController: TeamController = wire[TeamController]
  lazy val teamRepository: TeamRepository = wire[MongoDBTeamRepository]

  lazy val battleController: BattleController = wire[BattleController]
  lazy val battleRepository: BattleRepository = wire[MongoDBBattleRepository]

  lazy val rankingController: RankingController = wire[RankingController]
  lazy val playerRecordService: PlayerRecordService = wire[PlayerRecordService]

  lazy val competitionController: CompetitionController = wire[CompetitionController]

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
  executionContext: scala.concurrent.ExecutionContext) extends MessagesControllerComponents

class WebGatewayMessagesActionBuilder(parser: BodyParser[AnyContent], messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends MessagesActionBuilderImpl(parser, messagesApi) with MessagesActionBuilder {
  def this(parser: BodyParsers.Default, messagesApi: MessagesApi)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent], messagesApi)
  }
}

trait DBComponents {
  def config: Config
  lazy val driver: MongoDriver = new MongoDriver(Some(config), None)
  lazy val db: Future[DefaultDB] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val parsedUri = MongoConnection.parseURI(config.getString("mongodb.uri"))

    for {
      uri <- Future.fromTry(parsedUri)
      con = driver.connection(uri)
      dn <- Future(uri.db.get)
      db <- con.database(dn)
    } yield db
  }
}

class WebGatewayLoader extends ApplicationLoader {
  def load(context: Context): Application = (new WebGateway(context) with LagomDevModeComponents).application
}
