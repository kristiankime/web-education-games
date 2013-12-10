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

object DerivativeQuestionAnswerController extends Controller {

	def answers(id: Long) = DBAction { implicit dbSessionRequest =>
		// TODO can be null
		Ok(views.html.self_quiz_question_answers(DerivativeQuestionsModel.read(id).get, DerivativeQuestionAnswersModel.read(id)))
	}

	def answer(qid: Long, aid: Long, sid: Option[Long]) = DBAction { implicit dbSessionRequest =>
		val question = DerivativeQuestionsModel.read(qid).get // TODO can be null
		val answer = DerivativeQuestionAnswersModel.read(qid, aid)
		val set = sid.flatMap(DerivativeQuestionSetsModel.read(_).map(_._1))
		Ok(views.html.self_quiz_answer(question, answer, set))
	}

	def newAnswer(qid: Long, sid: Option[Long]) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(DerivativeQuestionsModel.read(qid).get, None, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = DerivativeQuestionsModel.read(qid).get // TODO check for no question here
				val mathML = MathML(answerForm._1).get // TODO can fail here
				val rawStr = answerForm._2
				val synched = answerForm._3

				val answerId = DerivativeQuestionAnswersModel.create(question, rawStr, mathML, synched)
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
