package clients

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.{Json, JsObject}
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class GeolocationClient @Inject()(httpClient: WSClient) (implicit executionContext: ExecutionContext) {

  private final val logger = Logger(this.getClass)

  private final val GEOCODE_URI = "http://maps.google.com/maps/api/geocode/json?address"

  private final val DISTANCE_MATRIX_URI = "https://maps.googleapis.com/maps/api/distancematrix/json"

  def get(address: String): Future[(Float, Float)] = {
    val uri = s"$GEOCODE_URI=$address"

    httpClient.url(uri)
      .withRequestTimeout(10000.millis).get().map { response =>
      val json = response.json
      val status = (json \ "status").as[String]

      logger.info(s"request call -> $uri status: ${response.status} body -> ${Json.stringify(json)}")

      if(status == "OK") {
        val results = (json \ "results").as[Seq[JsObject]]
        val geometry = (results.head \ "geometry").as[JsObject]
        val location = (geometry \ "location").as[JsObject]

        ((location \ "lat").as[Float], (location \ "lng").as[Float])
      } else throw new Exception("Address not found in google maps")
    }
  }

  def calculateDistance(originLat: Float, originLng: Float, destinationLat: Float, destinationLng: Float): Future[(Float, Float)] = {
    val uri = s"$DISTANCE_MATRIX_URI?origins=$originLat,$originLng&destinations=$destinationLat,$destinationLng"

    httpClient.url(uri)
      .withRequestTimeout(10000.millis).get().map { response =>
        val json = response.json
        val status = (json \ "status").as[String]

        logger.info(s"request call -> $uri status: ${response.status} body -> ${Json.stringify(json)}")

        if(status == "OK") {
          val rows = (json \ "rows").as[Seq[JsObject]]

          val elements = (rows.head \ "elements").as[Seq[JsObject]]
          val distanceJs = (elements.head \ "distance").as[JsObject]
          val durationJs = (elements.head \ "duration").as[JsObject]

          val distanceInMeters = (distanceJs \ "value").as[Double]
          val timeInSeconds = (durationJs \ "value").as[Double]

          ((distanceInMeters * 0.001).toFloat, (timeInSeconds * 0.0166667).toFloat)
        } else throw new Exception("Distance and time not found in google maps")
      }
  }
}
