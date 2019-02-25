package controllers

import java.util.UUID

import domain.{ Grouping, GroupingID, GroupingRepository }
import play.api.libs.json.{ Json, OFormat }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class GroupingController(mcc: MessagesControllerComponents, repository: GroupingRepository)(
  implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) {

  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolveBy(GroupingID(id)).map { grouping =>
      Ok(Json.toJson[GroupingResource](grouping))
    }
  }

  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.deleteBy(GroupingID(id)).map { _ =>
      NoContent
    }
  }

  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repository.resolve.map { groupings =>
      Ok(Json.toJson[Seq[GroupingResource]](groupings))
    }
  }

  def post(): Action[GroupingResource] = Action.async(parse.json[GroupingResource]) {
    implicit request: MessagesRequest[GroupingResource] =>
      val groupingResource = request.body
      repository.store(groupingResource).map { grouping =>
        Created(Json.toJson[GroupingResource](grouping))
      }
  }

  def put(id: UUID): Action[GroupingResource] = Action.async(parse.json[GroupingResource]) {
    implicit request: MessagesRequest[GroupingResource] =>
      val groupingResource = request.body
      repository.store(groupingResource).map { grouping =>
        Accepted(Json.toJson[GroupingResource](grouping))
      }
  }
}

case class GroupingResource(id: Option[UUID], teams: Seq[TeamResource])
object GroupingResource {
  implicit val format: OFormat[GroupingResource] = Json.format[GroupingResource]
  implicit def toEntity(groupingResource: GroupingResource): Grouping = {
    val id = groupingResource.id.map(GroupingID.apply).getOrElse(GroupingID.generate)
    Grouping(id, groupingResource.teams)
  }
  implicit def fromEntity(grouping: Grouping): GroupingResource =
    GroupingResource(Some(grouping.id.value), grouping.teams)
}
