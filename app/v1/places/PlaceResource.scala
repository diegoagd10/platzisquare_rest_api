package v1.places

import play.api.libs.json.{JsValue, Json, Writes}

case class PlaceResource(
  id: Long,
  name: String,
  description: String,
  street: String,
  streetNumber: Int,
  city: String,
  country: String,
  lat: Float,
  lng: Float,
  distance: Float = 0,
  duration: Float = 0,
  distanceLevel: Short = 0,
  created: Long = 0,
  updated: Long = 0
)

object PlaceResource {
  implicit val implicitWrites: Writes[PlaceResource] = new Writes[PlaceResource] {
    def writes(place: PlaceResource): JsValue = {
      Json.obj(
        "id" -> place.id,
        "name" -> place.name,
        "description" -> place.description,
        "street" -> place.street,
        "streetNumber" -> place.streetNumber,
        "city" -> place.city,
        "country" -> place.country,
        "lat" -> place.lat,
        "lng" -> place.lng,
        "distance" -> place.distance,
        "duration" -> place.duration,
        "distanceLevel" -> place.distanceLevel,
        "created" -> place.created,
        "updated" -> place.updated
      )
    }
  }
}
