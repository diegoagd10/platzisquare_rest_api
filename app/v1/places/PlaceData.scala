package v1.places

case class PlaceData(
  id: Long = 0,
  name: String,
  description: String,
  street: String,
  streetNumber: Int,
  city: String,
  country: String,
  lat: Float,
  lng: Float,
  created: Long = 0,
  updated: Long = 0
)
