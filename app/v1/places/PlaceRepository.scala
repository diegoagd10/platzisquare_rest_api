package v1.places

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class PlaceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  (implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PlaceTable(tag: Tag) extends Table[PlaceData](tag, "Places") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def street = column[String]("street")

    def streetNumber = column[Int]("streetNumber")

    def city = column[String]("city")

    def country = column[String]("country")

    def lat = column[Float]("lat")

    def lng = column[Float]("lng")

    def created = column[Long]("created")

    def updated = column[Long]("updated")

    def * = (id, name, description, street, streetNumber, city, country, lat, lng, created, updated) <> ((PlaceData.apply _).tupled, PlaceData.unapply)
  }

  private val places = TableQuery[PlaceTable]

  def create(data: PlaceData): DBIO[PlaceData] = {
    val now = System.currentTimeMillis()

    (places returning places.map(_.id)
      into ((place, id) => place.copy(id))) += data.copy(created = now, updated = now)
  }

  def update(data: PlaceData): DBIO[Int] = places
    .filter(_.id === data.id)
    .update(data.copy(updated = System.currentTimeMillis()))

  def list(page: Int, limit: Int): DBIO[Seq[PlaceData]] = places.drop((page - 1) * limit).take(limit).result

  def getById(id: Long): DBIO[Option[PlaceData]] = places.filter(_.id === id).result.headOption
}
