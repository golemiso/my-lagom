package com.golemiso.mylagom.model

import java.util.UUID

import play.api.libs.json.{ Format, Json }

case class Player(id: Player.Id, slug: Player.Slug, name: Player.Name)
object Player {
  implicit val format: Format[Player] = Json.format

  case class Id(id: UUID) extends AnyVal
  object Id {
    implicit val format: Format[Id] = Json.valueFormat
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

case class PlayerScore(id: Player.Id, totalScore: PlayerScore.Score, scoresByMode: Map[String, PlayerScore.Score])
object PlayerScore {
  implicit val format: Format[PlayerScore] = Json.format

  case class Score(score: Int = 0) extends AnyVal
  object Score {
    implicit val format: Format[Score] = Json.valueFormat
  }
}
