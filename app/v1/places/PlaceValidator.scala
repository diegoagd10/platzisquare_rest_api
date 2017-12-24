package v1.places

import play.api.data.Form
import play.api.mvc.{Result, Results}

class PlaceValidator extends Results {

  private val form: Form[PlaceFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText,
        "description" -> nonEmptyText,
        "street" -> nonEmptyText,
        "streetNumber" -> number,
        "city" -> nonEmptyText,
        "country" -> nonEmptyText
      )(PlaceFormInput.apply)(PlaceFormInput.unapply)
    )
  }

  def validate[A]()(implicit request: PlaceRequest[A]): Either[Result, PlaceFormInput] = {
    def failure(badForm: Form[PlaceFormInput]) = Left {
      BadRequest(badForm.errorsAsJson)
    }

    def success(input: PlaceFormInput) = Right {
      input
    }

    form.bindFromRequest().fold(failure, success)
  }
}
