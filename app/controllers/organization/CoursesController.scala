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

	def list = TODO

	def add = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseAdd())
	}

	def create = SecuredAction { implicit request =>
		implicit val user = User(request)
		CourseHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.organization.courseList(Courses.coursesAndEnrollment)),
			form => {
				val course = Courses.create(user, CourseTmp(form._1, DateTime.now))
				Ok(views.html.organization.courseView(course))
			})
	}

	def view(id: CourseId) = TODO

	def edit(id: CourseId) = TODO

	def update(id: CourseId) = TODO

	def delete(id: CourseId) = TODO

	def courseList = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.organization.courseList(Courses.coursesAndEnrollment))
	}

	def courseDetails(id: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Courses.coursesAndEnrollment(id) match {
			case Some((course, enrolled, quizes)) => Ok(views.html.organization.courseDetails(course, enrolled, quizes))
			case None => Ok("")
		}
	}

	def createCourse = SecuredAction { implicit request =>
		implicit val user = User(request)
		CourseHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.organization.courseList(Courses.coursesAndEnrollment)),
			form => {
				val id = Courses.createCourse(user, CourseTmp(form._1, DateTime.now), form._2.map(QuizId(_)): _*)
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