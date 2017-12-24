package v1.places

import javax.inject.Inject

import play.api.mvc.{BaseController, ControllerComponents}

class PlaceBaseController @Inject()(pcc: PlaceControllerComponents) extends BaseController {
  override protected def controllerComponents: ControllerComponents = pcc

  def PlaceAction: PlaceActionBuilder = pcc.placeActionBuilder

  def placeResourceHandler: PlaceResourceHandler = pcc.placeResourceHandler

  def placeValidator: PlaceValidator = pcc.placeValidator
}
