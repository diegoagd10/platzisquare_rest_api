package v1.places

import play.api.i18n.MessagesApi
import play.api.mvc.{Request, WrappedRequest}

class PlaceRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest[A](request) with PlaceRequestHeader
