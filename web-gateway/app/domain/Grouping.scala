package domain

import java.util.UUID

import scala.concurrent.Future

case class Grouping(id: GroupingID, teams: Seq[Team]) extends Entity[GroupingID]

case class GroupingID(value: UUID) extends IdObject
object GroupingID {
  def generate = GroupingID(UUID.randomUUID)
}

trait GroupingRepository extends Repository[Future, GroupingID, Grouping]
