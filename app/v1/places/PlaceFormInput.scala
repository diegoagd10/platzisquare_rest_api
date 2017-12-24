package v1.places

case class PlaceFormInput(
  id: Option[Long],
  name: String,
  description: String,
  street: String,
  streetNumber: Int,
  city: String,
  country: String
)
