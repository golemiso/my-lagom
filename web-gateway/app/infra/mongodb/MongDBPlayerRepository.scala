package infra.mongodb

import java.util.UUID

import domain._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ ExecutionContext, Future }

class MongoDBPlayerRepository(db: Future[DefaultDB])(implicit ec: ExecutionContext) extends PlayerRepository {

  def playersCollection: Future[BSONCollection] = db.map(_.collection("players"))

  import reactivemongo.bson._

  override def resolve: Future[Seq[Player]] = {
    for {
      collection <- playersCollection
      player <- collection
        .find(BSONDocument()).cursor[PlayerDocument]().collect[Seq](1000, Cursor.FailOnError[Seq[PlayerDocument]]())
    } yield player
  }

  override def resolveBy(id: PlayerID): Future[Player] = {
    val query = BSONDocument("_id" -> id.value)
    for {
      collection <- playersCollection
      player <- collection.find(query).requireOne[PlayerDocument]
    } yield player
  }

  override def store(player: Player): Future[Player] = {
    val selector = BSONDocument("_id" -> player.id.value)
    for {
      collection <- playersCollection
      _ <- collection.update[BSONDocument, PlayerDocument](selector, player, upsert = true)
    } yield player
  }

  override def deleteBy(id: PlayerID): Future[Unit] = {
    val selector = BSONDocument("_id" -> id.value)
    playersCollection.map(_.findAndRemove(selector))
  }

  override def resolvePlayerRecords: Future[Seq[PlayerRecord]] = {
    for {
      collection <- playersCollection
      player <- {
        import collection.BatchCommands.AggregationFramework._
        collection
          .aggregate(
            Lookup(from = "battles", localField = "_id", foreignField = "victory.players._id", as = "victory"),
            List(
              Lookup(from = "battles", localField = "_id", foreignField = "defeat.players._id", as = "defeat"),
              Project(
                BSONDocument(
                  "_id" -> 0,
                  "player._id" -> "$_id",
                  "player.name" -> "$name",
                  "record.victory" -> BSONDocument("$size" -> "$victory"),
                  "record.defeat" -> BSONDocument("$size" -> "$defeat")
                ))
            )
          ).map(_.head[PlayerRecordDocument].toSeq)
      }
    } yield player
  }

  override def resolvePlayerBattles: Future[Seq[PlayerBattles]] = {
    for {
      collection <- playersCollection
      playerBattles <- {
        import collection.BatchCommands.AggregationFramework._
        collection
          .aggregatorContext[PlayerBattlesDocument](Lookup(
            from = "battles",
            localField = "_id",
            foreignField = "teams.players._id",
            as = "battles")).prepared.cursor.collect[Seq]()
      }
    } yield playerBattles
  }
}

case class PlayerDocument(_id: UUID, name: String)
object PlayerDocument {
  implicit val handler: BSONDocumentHandler[PlayerDocument] = Macros.handler[PlayerDocument]
  implicit def toEntity(player: PlayerDocument): Player = Player(PlayerID(player._id), player.name)
  implicit def fromEntity(player: Player): PlayerDocument = PlayerDocument(player.id.value, player.name)
}

case class RecordDocument(victory: Int, defeat: Int)
object RecordDocument {
  implicit val reader: BSONDocumentReader[RecordDocument] = Macros.reader[RecordDocument]
  implicit def toEntity(record: RecordDocument): Record = Record(record.victory, record.defeat)
}

case class PlayerRecordDocument(player: PlayerDocument, record: RecordDocument)
object PlayerRecordDocument {
  implicit val reader: BSONDocumentReader[PlayerRecordDocument] = Macros.reader[PlayerRecordDocument]
  implicit def toEntity(playerRecord: PlayerRecordDocument): PlayerRecord =
    PlayerRecord(playerRecord.player, playerRecord.record)
}

case class PlayerBattlesDocument(_id: UUID, name: String, battles: Seq[BattleDocument])
object PlayerBattlesDocument {
  implicit val reader: BSONDocumentReader[PlayerBattlesDocument] = Macros.reader[PlayerBattlesDocument]
  implicit def toEntity(playerBattles: PlayerBattlesDocument): PlayerBattles =
    PlayerBattles(Player(PlayerID(playerBattles._id), playerBattles.name), playerBattles.battles)
}
