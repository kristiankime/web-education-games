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
import models.id.Ids._

object AnswerController extends Controller with SecureSocial {

	def answers(id: Long) = SecuredAction { implicit request =>
		val qid =  QuestionId(id)
		val question = Questions.findQuestion(qid).get // TODO can be null
		Ok(views.html.self_quiz_question_answers(question, Answers.findAnswers(qid)))
	}

	def answer(q: Long, aid: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		val qid =  QuestionId(q)
		val question = Questions.findQuestion(qid).get // TODO can be null
		val answer = Answers.findAnswer(qid, AnswerId(aid))
		val set = sid.flatMap(Quizzes.findQuiz(_))
		Ok(views.html.self_quiz_answer(question, answer, set))
	}

	def newAnswer(q: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		val qid =  QuestionId(q)
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
				Redirect(routes.AnswerController.answer(question.id.v, answerId.v, sid))
			})
	}

}

object AnswerHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
