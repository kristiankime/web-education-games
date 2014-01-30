package controllers.question.derivative

import scala.slick.session.Session
import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.table._
import service.User
import models.question.derivative._
import models.id._
import models.organization.Courses
import org.joda.time.DateTime
import service._

object QuestionsController extends Controller with SecureSocial {

	def view(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		
		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)
		val access = courseOp.flatMap(c => Courses.checkAccess(c.id)).getOrElse(Own) // TODO get access right
		
		(courseOp, quizOp, questionOp) match {
			case (Some(course), Some(quiz), Some(question)) => Ok(views.html.question.derivative.questionView(access, Some(course), quiz, question, None, Questions.findAnswers(questionId)))
			case (None, Some(quiz), Some(question)) => Ok(views.html.question.derivative.questionView(access, None, quiz, question, None, Questions.findAnswers(questionId)))
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(user, QuestionTmp(mathML, form._2, form._3, DateTime.now), quizId)
				Redirect(routes.QuizzesController.view(quizId, courseId))
			})
	}

}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val values = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
