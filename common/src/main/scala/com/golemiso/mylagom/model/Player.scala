package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json.{ Format, Json, Reads, Writes }

case class Player(id: Player.Id, slug: Player.Slug, name: Player.Name)
object Player {
  implicit val format: Format[Player] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
    implicit def anyValToUUID(id: Id): UUID = id.id
    implicit def UUIDToAnyVal(id: UUID): Id = Id(id)
  }

  case class Slug(slug: String) extends AnyVal
  object Slug {
    implicit val format: Format[Slug] = Json.valueFormat
  }

  case class Name(name: String) extends AnyVal
  object Name {
    implicit val format: Format[Name] = Json.valueFormat
  }
}

case class PlayerRankings(totalRanking: Seq[PlayerRanking], rankingsByMode: Seq[PlayerRankings.RankingsByMode])
object PlayerRankings {
  implicit val format: Format[PlayerRankings] = Json.format

  case class RankingsByMode(mode: Settings.Mode.Id, rankings: Seq[PlayerRanking])
  object RankingsByMode {
    implicit val format: Format[RankingsByMode] = Json.format
  }
}

case class PlayerRanking(player: Player.Id, ranking: PlayerRanking.Ranking)
object PlayerRanking {
  implicit val format: Format[PlayerRanking] = Json.format

  case class Ranking(ranking: Int) extends AnyVal
  object Ranking {
    implicit val format: Format[Ranking] = Json.valueFormat
  }
}

sealed abstract class RankBy(val value: String)
object RankBy {
  case object ModeScores extends RankBy("mode_scores")
  case object EntireScores extends RankBy("entire_scores")
  case object Unknown extends RankBy("unknown")
  val all: Seq[RankBy] = ModeScores :: EntireScores :: Nil
  def apply(value: String): RankBy = all.find(_.value == value).getOrElse(Unknown)
  def unapply(mode: RankBy): Option[String] = Some(mode.value)

  implicit val format: Format[RankBy] =
    Format(Reads.StringReads.map(RankBy.apply), Writes.StringWrites.contramap(_.value))
}
