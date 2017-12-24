package v1.places

import api.MessagesTypes._
import api.Responses
import javax.inject.{Inject, Singleton}

import clients.GeolocationClient
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Result
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlaceResourceHandler @Inject()(dbConfigProvider: DatabaseConfigProvider, geoClient: GeolocationClient, repository: PlaceRepository)
  (implicit executionContext: ExecutionContext) extends Responses {

  private final val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def show(id: Long, lat: Option[Float], lng: Option[Float]): Future[Either[Result, PlaceResource]] = {
    if(lat.isDefined && lng.isDefined) {
      val dbAction = repository.getById(id).transactionally

      db.run(dbAction).flatMap {
        case Some(found) =>
          geoClient.calculateDistance(lat.get, lng.get, found.lat, found.lng).map { case (distance, duration) =>
            Right {
              createPlaceResource(found, distance, duration)
            }
          } recover {
            case e: Throwable =>
              Left {
                BadRequest("Distance and time not found in google maps by invalid coordinates ", INVALID_REQUEST)
              }
          }

        case None =>
          Future.successful {
            Left {
              NotFound(s"Place not found with id $id", RESOURCE_NOT_FOUND)
            }
          }
      }
    } else Future.successful {
      Left {
        BadRequest("Missing latitude and longitude values", INVALID_REQUEST)
      }
    }
  }

  def all(lat: Option[Float], lng: Option[Float], page: Int, limit: Int): Future[Either[Result, Seq[PlaceResource]]] = {
    if(lat.isDefined && lng.isDefined) {
      val dbAction = repository.list(page, limit).transactionally

      db.run(dbAction).flatMap { places =>
        val futureCalls = places.map { p =>
          geoClient.calculateDistance(lat.get, lng.get, p.lat, p.lng).map { case (distance, duration) =>
            createPlaceResource(p, distance, duration)
          }
        }

        Future.sequence(futureCalls).flatMap { placesResources =>
          Future.successful {
            Right {
              placesResources
            }
          }
        }
      }
    } else Future.successful {
      Left {
        BadRequest("Missing latitude and longitude values", INVALID_REQUEST)
      }
    }
  }

  def create(placeInput: PlaceFormInput): Future[Either[Result, PlaceResource]] = {
    val address = buildAddress(placeInput)

    geoClient.get(address).flatMap { case (lat, lng) =>
      val newPlace = PlaceData(
          name = placeInput.name,
          description = placeInput.description,
          street = placeInput.street,
          streetNumber = placeInput.streetNumber,
          city = placeInput.city,
          country = placeInput.country,
          lat = lat,
          lng = lng
        )

      val dbAction = repository.create(newPlace).transactionally

      db.run(dbAction).map { created =>
        Right {
          createPlaceResource(created)
        }
      }
    } recover {
        case e: Throwable =>
          Left {
            BadRequest("Address not found in google maps.", INVALID_REQUEST)
          }
    }
  }

  def put(id: Long, placeInput: PlaceFormInput): Future[Either[Result, Boolean]] = {
    if(placeInput.id.isDefined && id == placeInput.id.get) {
      val address = buildAddress(placeInput)

      geoClient.get(address).flatMap { case (lat, lng) =>
        val newPlace = PlaceData(
          id = id,
          name = placeInput.name,
          description = placeInput.description,
          street = placeInput.street,
          streetNumber = placeInput.streetNumber,
          city = placeInput.city,
          country = placeInput.country,
          lat = lat,
          lng = lng
        )

        db.run(repository.getById(id)).flatMap {
          case Some(found) =>
            val dbAction = repository.update(newPlace).transactionally

            db.run(dbAction).map { result =>
              Right {
                result > 0
              }
            }

          case None =>
            Future.successful {
              Left {
                NotFound(s"Place not found with id $id", RESOURCE_NOT_FOUND)
              }
            }
        }
      } recover {
        case e: Throwable =>
          Left {
            BadRequest("Address not found in google maps.", INVALID_REQUEST)
          }
      }
    } else Future.successful {
      Left {
        BadRequest("The asked resource is different from the one in the body", INVALID_REQUEST)
      }
    }
  }

  private def buildAddress(placeInput: PlaceFormInput): String = s"${placeInput.street} ${placeInput.streetNumber}, ${placeInput.city}, ${placeInput.country}"

  private def createPlaceResource(placeData: PlaceData, distance: Float = 0, duration: Float = 0): PlaceResource = PlaceResource(
    id = placeData.id,
    name = placeData.name,
    description = placeData.description,
    street = placeData.street,
    streetNumber = placeData.streetNumber,
    city = placeData.city,
    country = placeData.country,
    lat = placeData.lat,
    lng = placeData.lng,
    distance = distance,
    duration = duration,
    distanceLevel = calculateDistanceLevel(distance),
    created = placeData.created,
    updated = placeData.updated
  )

  private def calculateDistanceLevel(distance: Float): Short = {
    if (distance < 3) 1
    else if (distance < 6) 2
    else 3
  }
}
