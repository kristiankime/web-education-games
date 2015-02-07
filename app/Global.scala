import play.api._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

object Global extends GlobalSettings {

	override def onBadRequest(request: RequestHeader, error: String) = {
		Future.successful(BadRequest("Bad Request: " + error))
	}

	override def onError(request: RequestHeader, ex: Throwable) = {
		Future.successful(InternalServerError(views.html.errors.errorPage(ex)))
	}

	override def onHandlerNotFound(request: RequestHeader) = {
		Future.successful(NotFound(views.html.errors.notFoundPage(request.path)))
	}

}