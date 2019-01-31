package infra.mongodb

import java.util.UUID

import domain._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

class MongoDBTeamRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends TeamRepository {

  def teamsCollection: Future[BSONCollection] = db.map(_.collection("teams"))

  override def resolve: Future[Seq[Team]] = for {
    connection <- teamsCollection
    teams <- connection.find(BSONDocument()).cursor[TeamDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[TeamDocument]]())
  } yield teams

  override def resolveBy(id: TeamID): Future[Team] = {
    val query = BSONDocument("_id" -> id.value)

    for {
      team <- teamsCollection.flatMap(_.find(query).requireOne[TeamDocument])
    } yield team
  }

  override def store(team: Team): Future[Team] = {
    teamsCollection.flatMap(_.insert[TeamDocument](team).map(_ => team))
  }

  override def deleteBy(id: TeamID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    teamsCollection.map(_.findAndRemove(selector))
  }
}

case class TeamDocument(_id: UUID, players: Seq[PlayerDocument])
object TeamDocument {
  implicit val handler: BSONDocumentHandler[TeamDocument] = Macros.handler[TeamDocument]
  implicit def toEntity(team: TeamDocument): Team = Team(TeamID(team._id), team.players)
  implicit def fromEntity(team: Team): TeamDocument = TeamDocument(team.id.value, team.players)
}
