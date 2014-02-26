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
			errors => BadRequest(views.html.index()),
			form => {
				val quizId = Quizzes.create(QuizTmp(user.id, form, DateTime.now), courseId)
				Redirect(routes.QuizzesController.view(quizId, courseId))
			})
	}

	def view(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val access = courseOp.flatMap(c => Courses.checkAccess(c.id)).getOrElse(Own) // TODO get access right
		
		(quizOp, courseOp) match {
			case (Some(quiz), Some(course)) => Ok(views.html.question.derivative.quizView(access, Some(course), quiz, Quizzes.findQuestions(quizId)))
			case (Some(quiz), None) => Ok(views.html.question.derivative.quizView(access, None, quiz, Quizzes.findQuestions(quizId)))
			case _ => BadRequest(views.html.index())
		}
	}
	
	def rename(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		QuizForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
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