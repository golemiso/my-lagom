package service

import domain.{ PlayerRecord, PlayerRepository, Record }

import scala.concurrent.{ ExecutionContext, Future }

class PlayerRecordService(playerRepository: PlayerRepository)(implicit ec: ExecutionContext) {
  def getAll: Future[Seq[PlayerRecord]] = playerRepository.resolvePlayerBattles.map { pbs =>
    pbs.map { pb =>
      val player = pb.player
      val battles = pb.battles
      val record = battles.map { b =>
        b.teams.find(_.players.contains(pb.player)) match {
          case Some(t) if b.result.exists(_.victory == t.id) => Record(1, 0)
          case Some(t) if b.result.exists(_.defeat == t.id)  => Record(0, 1)
          case _                                             => Record(0, 0)
        }
      }.foldLeft(Record(0, 0))((a, b) => Record(victory = a.victory + b.victory, defeat = a.defeat + b.defeat))
      PlayerRecord(player, record)
    }
  }
}
