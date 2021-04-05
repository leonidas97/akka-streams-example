package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class DogsFactsController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  def index(): Action[AnyContent] =
    Action {
      Ok("alive")
    }
}
