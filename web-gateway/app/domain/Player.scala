package domain

import java.util.UUID

import scala.concurrent.Future

case class Player(id: PlayerID, name: String) extends Entity[PlayerID]

case class PlayerID(value: UUID) extends IdObject
object PlayerID {
  def generate = PlayerID(UUID.randomUUID)
}

trait PlayerRepository extends Repository[Future, PlayerID, Player] {
  def resolvePlayerRecords: Future[Seq[PlayerRecord]]
  def resolvePlayerBattles: Future[Seq[PlayerBattles]]
}

case class PlayerRecord(player: Player, record: Record)
case class Record(victory: Int, defeat: Int)

case class PlayerBattles(player: Player, battles: Seq[Battle])