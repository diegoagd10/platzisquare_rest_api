package v1.places

import javax.inject.Inject

import play.api.http.HttpVerbs
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PlaceActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                                  (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[PlaceRequest, AnyContent]
  with HttpVerbs {

  val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type PlaceRequestBlock[A] = PlaceRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A], block: PlaceRequestBlock[A]): Future[Result] = {
    val future = block(new PlaceRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}
