package controllers

import scala.slick.session.Session
import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial

object DerivativeQuestionAnswerController extends Controller with SecureSocial {

	def answers(id: Long) = SecuredAction { implicit request =>
		val question = Questions.findQuestion(id).get // TODO can be null
		Ok(views.html.self_quiz_question_answers(question, Answers.findAnswers(id)))
	}

	def answer(qid: Long, aid: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		val question = Questions.findQuestion(qid).get // TODO can be null
		val answer = Answers.findAnswer(qid, aid)
		val set = sid.flatMap(Quizes.findQuiz(_))
		Ok(views.html.self_quiz_answer(question, answer, set))
	}

	def newAnswer(qid: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(Questions.findQuestion(qid).get, None, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = Questions.findQuestion(qid).get // TODO check for no question here
				val mathML = MathML(answerForm._1).get // TODO can fail here
				val rawStr = answerForm._2
				val synched = answerForm._3

				val answerId = Answers.createAnswer(question, rawStr, mathML, synched)
				Redirect(routes.DerivativeQuestionAnswerController.answer(question.id, answerId, sid))
			})
	}

}

object DerivativeQuestionAnswerHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
