package controllers.organization

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import scala.util.Random
import service._
import models.support._
import models.organization._
import models.question.derivative._
import controllers.support.SecureSocialDB
import org.joda.time.DateTime

object CoursesController extends Controller with SecureSocialDB {
	val randomEngine = new Random(DateTime.now.getMillis())

	def list = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseList(Courses.listDetails))
	}

	def add = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseAdd())
	}

	def create = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		CourseCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Courses.listDetails)),
			form => {
				val editCode = "CO-E-" + randomEngine.nextInt(100000)
				val viewCode = "CO-V-" + randomEngine.nextInt(100000)
				val course = Courses.create(CourseTmp(form, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(course.id))
			})
	}

	def view(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Courses.findDetails(id) match {
			case Some(courseDetails) => Ok(views.html.organization.courseView(courseDetails, Assignments.find(id), Quizzes.findByCourse(id)))
			case None => BadRequest(views.html.index(Courses.listDetails))
		}
	}

	def join(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		CourseJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Courses.listDetails)),
			form => {
				Courses.find(id) match {
					case Some(course) => {
						if (course.editCode == form) {
							Courses.grantAccess(course, Edit)
						} else if (course.viewCode == form) {
							Courses.grantAccess(course, View)
						}
						Redirect(routes.CoursesController.view(course.id)) // TODO indicate failure in a better fashion
					}
					case None => BadRequest(views.html.index(Courses.listDetails))
				}

			})
	}

}

object CourseCreate {
	val name = "name"
	val form = Form(name -> nonEmptyText)
}

object CourseJoin {
	val code = "code"
	val form = Form(code -> text)
}
