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

object QuizzesController extends Controller with SecureSocial {
	def list = TODO

	def add(courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.question.derivative.quizAdd(courseId.flatMap(Courses.find(_))))
	}

	def create(courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		QuizForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val quizId = Quizzes.create(user, QuizTmp(form, DateTime.now), courseId)
				Redirect(routes.QuizzesController.view(quizId))
			})
	}

	def view(quizId: QuizId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Quizzes.find(quizId) match {
			case Some(quiz) => Ok(views.html.question.derivative.quizView(None, None, quiz, Own, Quizzes.findQuestions(quizId))) // TODO get access right
			case None => BadRequest(views.html.index())
		}
	}

	def update(quizId: QuizId) = TODO

	def delete(quizId: QuizId) = TODO
}

object QuizForm {
	val name = "name"

	val values = Form(name -> nonEmptyText)
}