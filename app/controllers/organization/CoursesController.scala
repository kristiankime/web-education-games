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

object CoursesController extends Controller with SecureSocial {

	def courseList = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseList(Courses.coursesAndEnrollment))
	}

	def courseDetails(id: Long) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Courses.coursesAndEnrollment(CourseId(id)) match {
			case Some((course, enrolled, quizes)) => Ok(views.html.organization.courseDetails(course, enrolled, quizes))
			case None => Ok("")
		}
	}

	def createCourse = SecuredAction { implicit request =>
		implicit val user = User(request)
		CourseHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.organization.courseList(Courses.coursesAndEnrollment)),
			form => {
				val id = Courses.createCourse(user, CourseTmp(form._1), form._2: _*)
				Courses.coursesAndEnrollment(id) match {
					case Some((course, enrolled, quizes)) => Ok(views.html.organization.courseDetails(course, enrolled, quizes))
					case None => Ok("")
				}
			})
	}
}

object CourseHTML {
	val name = "name"
	val quizId = "quizId"
	def quizId(i: Int) = "quizId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, quizId -> list(longNumber)))
}