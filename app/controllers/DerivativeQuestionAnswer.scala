package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.data._
import play.api.data.Forms._
import akka.actor._
import scala.concurrent.duration._
import models._
import mathml._
import scala.util._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import scala.slick.session.Session

//===
import play.api.Logger
import play.api.Play.current

object DerivativeQuestionAnswer extends Controller {

	def selfQuizAnswers(id: Long) = DBAction { implicit dbSessionRequest =>
		// TODO can be null
		Ok(views.html.self_quiz_question_answers(DerivativeQuestionsModel.read(id).get, DerivativeQuestionAnswersModel.read(id)))
	}

	def selfQuizAnswer(qid: Long, aid: Long) = DBAction { implicit dbSessionRequest =>
		val question = DerivativeQuestionsModel.read(qid).get // TODO can be null
		val answer = DerivativeQuestionAnswersModel.read(qid, aid)
		Ok(views.html.self_quiz_answer(question, answer))
	}

	def answerSelfQuizQuestion = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(DerivativeQuestionsModel.read(errors.get._1).get, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = DerivativeQuestionsModel.read(answerForm._1).get // TODO check for no question here
				val mathML = MathML(answerForm._2).get // TODO can fail here
				val rawStr = answerForm._3
				val synched = answerForm._4

				val answerId = DerivativeQuestionAnswersModel.create(question, rawStr, mathML, synched);
				Redirect(routes.DerivativeQuestionAnswer.selfQuizAnswer(question.id, answerId))
			})
	}

}

object DerivativeQuestionAnswerHTML {
	val questionId = "questionId"
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(questionId -> number, mathML -> text, rawStr -> text, current -> boolean))
}
