package controllers.question.derivative

import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.question.derivative._
import models.id._
import org.joda.time.DateTime
import models.organization.Courses
import service.Own

object AnswersController extends Controller with SecureSocial {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)

		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)
		val answerOp = Answers.find(answerId)
		val access = courseOp.flatMap(c => Courses.checkAccess(c.id)).getOrElse(Own) // TODO get access right

		(courseOp, quizOp, questionOp, answerOp) match {
			case (Some(course), Some(quiz), Some(question), Some(answer)) => Ok(views.html.question.derivative.questionView(access, Some(course), quiz, question, Some(answer), Questions.findAnswers(question.id)))
			case (None, Some(quiz), Some(question), Some(answer)) => Ok(views.html.question.derivative.questionView(access, None, quiz, question, Some(answer), Questions.findAnswers(question.id)))
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val question = Questions.findQuestion(questionId).get // TODO check for no question here
				val mathML = MathML(form._1).get // TODO can fail here
				val rawStr = form._2
				val synched = form._3
				val answer = Answers.createAnswer(AnswerTmp(user.id, question.id, mathML, rawStr,  synched, Answers.correct(question, mathML), DateTime.now))
				Redirect(routes.AnswersController.view(quizId, questionId, answer.id, courseId))
			})
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val values = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
