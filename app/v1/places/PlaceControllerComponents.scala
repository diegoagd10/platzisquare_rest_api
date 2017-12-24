package v1.places

import javax.inject.Inject

import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ControllerComponents, DefaultActionBuilder, PlayBodyParsers}

import scala.concurrent.ExecutionContext

case class PlaceControllerComponents @Inject()(
  placeActionBuilder: PlaceActionBuilder,
  placeResourceHandler: PlaceResourceHandler,
  placeValidator: PlaceValidator,
  actionBuilder: DefaultActionBuilder,
  parsers: PlayBodyParsers,
  messagesApi: MessagesApi,
  langs: Langs,
  fileMimeTypes: FileMimeTypes,
  executionContext: ExecutionContext
) extends ControllerComponents

