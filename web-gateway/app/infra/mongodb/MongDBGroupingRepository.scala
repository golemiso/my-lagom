package infra.mongodb

import java.util.UUID

import domain.{ Grouping, GroupingID, GroupingRepository }
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

class MongoDBGroupingRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends GroupingRepository {

  def groupingsCollection: Future[BSONCollection] = db.map(_.collection("groupings"))

  override def resolve: Future[Seq[Grouping]] = {
    for {
      collection <- groupingsCollection
      groupings <- collection
        .find(BSONDocument()).cursor[GroupingDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[GroupingDocument]]())
    } yield groupings
  }

  override def resolveBy(id: GroupingID): Future[Grouping] = {
    val query = BSONDocument("_id" -> id.value)
    for {
      collection <- groupingsCollection
      grouping <- collection.find(query).requireOne[GroupingDocument]
    } yield grouping
  }

  override def store(grouping: Grouping): Future[Grouping] = {
    groupingsCollection.flatMap(_.insert[GroupingDocument](grouping).map(_ => grouping))
  }

  override def deleteBy(id: GroupingID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    groupingsCollection.map(_.findAndRemove(selector))
  }
}

case class GroupingDocument(_id: UUID, teams: Seq[TeamDocument])
object GroupingDocument {
  implicit val handler: BSONDocumentHandler[GroupingDocument] = Macros.handler[GroupingDocument]
  implicit def toEntity(grouping: GroupingDocument): Grouping = Grouping(GroupingID(grouping._id), grouping.teams)
  implicit def fromEntity(grouping: Grouping): GroupingDocument = GroupingDocument(grouping.id.value, grouping.teams)
}
