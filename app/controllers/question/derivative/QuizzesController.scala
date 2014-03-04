package controllers.question.derivative

import scala.slick.session.Session
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import models.question.derivative._
import service._
import models.id._
import mathml.MathML
import org.joda.time.DateTime
import models.organization.Courses
import models.organization.Sections
import controllers.support.SecureSocialDB

object QuizzesController extends Controller with SecureSocialDB {

	def add(courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			Ok(views.html.question.derivative.quizAdd(courseId.flatMap(Courses.find(_))))
	}

	def create(courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			QuizForm.values.bindFromRequest.fold(
				errors => BadRequest(views.html.index(Courses.listDetails)),
				form => {
					val quiz = Quizzes.create(QuizTmp(user.id, form, DateTime.now), courseId)
					Redirect(routes.QuizzesController.view(quiz.id, courseId))
				})
	}

	def view(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			val courseOp = courseId.flatMap(Courses.find(_))
			val quizOp = Quizzes.find(quizId)
			val access = courseOp.map(_.access).getOrElse(Own) // TODO get access right

			(quizOp, courseOp) match {
				case (Some(quiz), Some(course)) => {
					val results = access.write(() => course.sectionResults(quiz))
					Ok(views.html.question.derivative.quizView(access, Some(course), quiz.results(user), results))
				}
				case (Some(quiz), None) => Ok(views.html.question.derivative.quizView(access, None, quiz.results(user), None))
				case _ => BadRequest(views.html.index(Courses.listDetails))
			}
	}

	def rename(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			QuizForm.values.bindFromRequest.fold(
				errors => BadRequest(views.html.index(Courses.listDetails)),
				form => {
					Quizzes.rename(quizId, form)
					Redirect(routes.QuizzesController.view(quizId, courseId))
				})
	}

}

object QuizForm {
	val name = "name"

	val values = Form(name -> nonEmptyText)
}