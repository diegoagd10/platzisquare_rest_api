package api

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.http.Status._
import play.api.mvc.{Result, Results}

trait Responses extends Results {
  private case class ApiMessage(description: String, messageType: String, user: Boolean, code: Int)

  private implicit val apiMessageWrites: Writes[ApiMessage] = new Writes[ApiMessage] {
    def writes(message: ApiMessage): JsValue = {
      Json.obj(
        "description" -> message.description,
        "type" -> message.messageType,
        "user" -> message.user,
        "code" -> message.code
      )
    }
  }

  def NotFound(description: String, messageType: String): Result = NotFound(Json.toJson(createApiMessage(description, messageType, NOT_FOUND)))

  def BadRequest(description: String, messageType: String): Result = NotFound(Json.toJson(createApiMessage(description, messageType, BAD_REQUEST)))

  private def createApiMessage(description: String, messageType: String, code: Int, user: Boolean = true): ApiMessage = ApiMessage(
    description = description,
    messageType = messageType,
    user = user,
    code = code
  )
}
