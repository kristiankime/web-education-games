package controllers.organization

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import securesocial.core.SecureSocial
import service.User
import models.organization._
import models.question.derivative._
import models.id._
import org.joda.time.DateTime

object CoursesController extends Controller with SecureSocial {

	def list = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseList(Courses.list))
	}

	def add = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseAdd())
	}

	def create = SecuredAction { implicit request =>
		implicit val user = User(request)
		CourseForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val courseId = Courses.create(user, CourseTmp(form, DateTime.now))
				Redirect(routes.CoursesController.view(courseId))
			})
	}

	def view(id: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Courses.find(id) match {
			case Some(course) => Ok(views.html.organization.courseView(course, Sections.findByCourse(id)))
			case None => BadRequest(views.html.index())
		}
	}

	def update(id: CourseId) = TODO

	def delete(id: CourseId) = TODO
}

/*
object CourseHTML {
	val name = "name"
	val quizId = "quizId"
	def quizId(i: Int) = "quizId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, quizId -> list(longNumber)))
} 
*/

object CourseForm {
	val name = "name"
	val values = Form(name -> nonEmptyText)
}