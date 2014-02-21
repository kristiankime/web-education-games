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
import controllers.support.SecureSocialDB

object CoursesController extends Controller with SecureSocialDB {
	val randomEngine = new Random(0L)

	def list = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseList(Courses.listDetails))
	}

	def add = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseAdd())
	}

	def create = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		CourseCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val editCode = "CO-E-" + randomEngine.nextInt(100000)
				val viewCode = "CO-V-" + randomEngine.nextInt(100000)
				val course = Courses.create(CourseTmp(form, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(course.id))
			})
	}

	def view(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Courses.findDetails(id) match {
			case Some(courseDetails) => Ok(views.html.organization.courseView(courseDetails, Quizzes.findByCourse(id)))
			case None => BadRequest(views.html.index())
		}
	}

	def join(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
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
						// TODO indicate failure in a better fashion
						Redirect(routes.CoursesController.view(course.id))
					}
					case None => BadRequest(views.html.index())
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
