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
import service._

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

	def join(id: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		CourseJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				Courses.find(id) match {
					case Some(course) => {
						if (course.editCode == form) {
							Courses.grantAccess(user, course, Edit)
						} else if (course.viewCode == form) {
							Courses.grantAccess(user, course, View)
						}
						// TODO indicate failure of code
						Redirect(routes.CoursesController.view(course.id))
					}
					case None => BadRequest(views.html.index())
				}

			})
	}

}

object CourseForm {
	val name = "name"
	val values = Form(name -> nonEmptyText)
}

object CourseJoin {
	val code = "code"
	val form = Form(code -> text)
}

object CourseAccess {
	def apply(access: Access) = access match {
		case Own => "Administrator"
		case Edit => "Teacher"
		case View => "Student"
		case Non => "None"
	}
}
