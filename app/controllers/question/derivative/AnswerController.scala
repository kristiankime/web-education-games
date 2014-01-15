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

object AnswerController extends Controller with SecureSocial {

	def answers(qid: QuestionId) = SecuredAction { implicit request =>
		val question = Questions.findQuestion(qid).get // TODO can be null
		Ok(views.html.self_quiz_question_answers(question, Answers.findAnswers(qid)))
	}

	def answer(qid: QuestionId, aid: AnswerId, sid: Option[QuizId]) = SecuredAction { implicit request =>
		val question = Questions.findQuestion(qid).get // TODO can be null
		val answer = Answers.findAnswer(qid, aid)
		val set = sid.flatMap(q => Quizzes.findQuiz(q))
		Ok(views.html.self_quiz_answer(question, answer, set))
	}

	def newAnswer(qid: QuestionId, sid: Option[QuizId]) = SecuredAction { implicit request =>
		AnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(Questions.findQuestion(qid).get, None, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = Questions.findQuestion(qid).get // TODO check for no question here
				val mathML = MathML(answerForm._1).get // TODO can fail here
				val rawStr = answerForm._2
				val synched = answerForm._3

				val answerId = Answers.createAnswer(User(request), question, rawStr, mathML, synched)
				Redirect(routes.AnswerController.answer(question.id, answerId, sid))
			})
	}

}

object AnswerHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
