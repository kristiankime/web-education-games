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

object AnswersController extends Controller with SecureSocial {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId) = SecuredAction { implicit request =>
		implicit val user = User(request)		
		(Quizzes.find(quizId), Questions.find(questionId), Answers.find(answerId)) match {
			case (Some(quiz), Some(question), Some(answer)) => {
				Ok(views.html.question.derivative.questionView(quiz, question, Some(answer), Questions.findAnswers(question.id)))
			}
			case _ => BadRequest(views.html.index())
		}
	}
	
	def create(quizId: QuizId, questionId: QuestionId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val question = Questions.findQuestion(questionId).get // TODO check for no question here
				val mathML = MathML(form._1).get // TODO can fail here
				val rawStr = form._2
				val synched = form._3
				val answerId = Answers.createAnswer(User(request), question, rawStr, mathML, synched)
				Redirect(routes.AnswersController.view(quizId, questionId, answerId))
			})
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val values = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
