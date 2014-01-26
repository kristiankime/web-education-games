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
import scala.util.Random

object CoursesController extends Controller with SecureSocial {
	val randomEngine = new Random(0L)
	
	def list = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseList(Courses.listDetails))
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
				val editCode = "CO-E-" + randomEngine.nextInt(100000)
				val viewCode = "CO-V-" + randomEngine.nextInt(100000)
				val course = Courses.create(CourseTmp(form, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(course.id))
			})
	}

	def view(id: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Courses.findDetails(id) match {
			case Some(courseDetails) => Ok(views.html.organization.courseView(courseDetails, Quizzes.findByCourse(id)))
			case None => BadRequest(views.html.index())
		}
	}

	def update(id: CourseId) = TODO

	def delete(id: CourseId) = TODO
}

object CourseForm {
	val name = "name"
	val values = Form(name -> nonEmptyText)
}