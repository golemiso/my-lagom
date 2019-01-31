package domain

import java.util.UUID

import scala.concurrent.Future

case class Team(id: TeamID, players: Seq[Player]) extends Entity[TeamID]

case class TeamID(value: UUID) extends IdObject
object TeamID {
  def generate = TeamID(UUID.randomUUID)
}

trait TeamRepository extends Repository[Future, TeamID, Team]
