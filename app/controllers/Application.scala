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

object Application extends Controller {

	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = Action {
		Ok(views.html.index())
	}

	// ======== Equations ======== 
	def equations = DBAction { implicit dbSessionRequest =>
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	def newEquation = DBAction { implicit dbSessionRequest =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(Equations.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.equations)
			})
	}

	def deleteEquation(id: Long) = DBAction { implicit dbSessionRequest =>
		Equations.delete(id)
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	// ======== Self Quiz Questions ======== 
	def selfQuiz = selfQuizQuestions

	def selfQuizQuestions = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_questions(DerivativeQuestions.all()))
	}

	def selfQuizQuestion(id: Long) = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_answer(DerivativeQuestions.read(id).get, None)) // TODO can be null
	}

	def newSelfQuizQuestion = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_questions(DerivativeQuestions.all())),
			form => {
				MathML(form._1).foreach(DerivativeQuestions.create(_, form._2, form._3))
				Redirect(routes.Application.selfQuizQuestions)
			})
	}

	def deleteSelfQuizQuestion(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestions.delete(id);
		Ok(views.html.self_quiz_questions(DerivativeQuestions.all()))
	}

	// ======== Self Quiz Answers ======== 
	def selfQuizAnswers(id: Long) = DBAction { implicit dbSessionRequest =>
		// TODO can be null
		Ok(views.html.self_quiz_question_answers(DerivativeQuestions.read(id).get, DerivativeQuestionAnswers.read(id)))
	}

	def selfQuizAnswer(qid: Long, aid: Long) = DBAction { implicit dbSessionRequest =>
		val question = DerivativeQuestions.read(qid).get // TODO can be null
		val answer = DerivativeQuestionAnswers.read(qid, aid)
		Ok(views.html.self_quiz_answer(question, answer))
	}

	def answerSelfQuizQuestion = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(DerivativeQuestions.read(errors.get._1).get, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = DerivativeQuestions.read(answerForm._1).get // TODO check for no question here
				val mathML = MathML(answerForm._2).get // TODO can fail here
				val rawStr = answerForm._3
				val synched = answerForm._4

				val answerId = DerivativeQuestionAnswers.create(question, rawStr, mathML, synched);
				Redirect(routes.Application.selfQuizAnswer(question.id, answerId))
			})
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}

object DerivativeQuestionHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}

object DerivativeQuestionAnswerHTML {
	val questionId = "questionId"
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(questionId -> number, mathML -> text, rawStr -> text, current -> boolean))
}
