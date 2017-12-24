package v1.places

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class PlaceController @Inject()(cc: PlaceControllerComponents)(implicit executionContext: ExecutionContext)
  extends PlaceBaseController(cc) {

  def show(id: Long, lat: Option[Float], lng: Option[Float]): Action[AnyContent] = PlaceAction.async { implicit request =>
    placeResourceHandler.show(id, lat, lng).map {
      case Right(place) =>
        Ok(Json.toJson(place))

      case Left(result) => result
    }
  }

  def all(lat: Option[Float], lng: Option[Float], page: Option[Int] = None, limit: Option[Int] = None): Action[AnyContent] = PlaceAction.async { implicit request =>
    placeResourceHandler.all(lat, lng, page.getOrElse(1), limit.getOrElse(5)).map {
      case Right(places) =>
        Ok(Json.toJson(places))

      case Left(result) => result
    }
  }

  def create: Action[AnyContent] = PlaceAction.async { implicit request =>
    placeValidator.validate() match {
      case Right(input) =>
        placeResourceHandler.create(input).map {
          case Right(place) =>
            Created(Json.toJson(place)).withHeaders(LOCATION -> s"/v1/places/${place.id}")

          case Left(result) => result
        }

      case Left(result) => Future.successful {
        result
      }
    }
  }

  def put(id: Long): Action[AnyContent] = PlaceAction.async { implicit request =>
    placeValidator.validate() match {
      case Right(input) =>
        placeResourceHandler.put(id, input).map {
          case Right(updated) =>
            if(updated) {
              NoContent
            } else InternalServerError("Place was not updated")

          case Left(result) => result
        }

      case Left(result) => Future.successful {
        result
      }
    }
  }
}
