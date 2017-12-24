package v1.places

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class PlaceRouter @Inject()(controller: PlaceController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/" ? q_o"lat=${float(lat)}" & q_o"lng=${float(lng)}" & q_o"page=${int(page)}" & q_o"limit=${int(limit)}") =>
      controller.all(lat, lng, page, limit)

    case POST(p"/") =>
      controller.create

    case GET(p"/${long(id)}" ? q_o"lat=${float(lat)}" & q_o"lng=${float(lng)}") =>
      controller.show(id, lat, lng)

    case PUT(p"/${long(id)}") =>
      controller.put(id)
  }
}
