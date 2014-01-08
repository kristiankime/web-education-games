package controllers.organization

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.organization._

object CoursesController extends Controller with SecureSocial {

	def courseList = SecuredAction { implicit request =>
		request.user match {
			case user: User => {
				val courses = Courses.coursesAndEnrollment(user)
				Ok(views.html.organization.courseList(courses))
			}
			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // TODO better handling then just throwing 
		}
	}

}
