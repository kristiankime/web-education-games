package controllers.organization

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.organization._

object CoursesController extends Controller with SecureSocial {

	def courseList = SecuredAction { implicit request =>
		Ok(views.html.organization.courseList(Courses.coursesAndEnrollment(User(request))))
	}

}
