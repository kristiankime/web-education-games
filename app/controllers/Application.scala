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
//import scala._
import scala.util._

object Application extends Controller {

	def index = Action {
		Ok(views.html.index())
	}

	// ======== Equations ======== 
	def equations = Action {
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	def newEquation = Action { implicit request =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(Equations.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.equations)
			})
	}

	def deleteEquation(id: Int) = Action {
		Equations.delete(id);
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	// ======== Self Quiz Questions ======== 
	def selfQuiz = Action {
		Ok(views.html.self_quiz_list(DerivativeQuestions.all()))
	}

	def selfQuizQuestions = Action {
		Ok(views.html.self_quiz_list(DerivativeQuestions.all()))
	}

	def selfQuizQuestion(id: Int) = Action {
		Ok(views.html.self_quiz_answer(DerivativeQuestions.read(id).get, None))
	}

	def newSelfQuizQuestion = Action { implicit request =>
		DerivativeQuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_list(DerivativeQuestions.all())),
			form => {
				MathML(form._1).foreach(DerivativeQuestions.create(_, form._2, form._3))
				Redirect(routes.Application.selfQuizQuestions)
			})
	}

	def deleteSelfQuizQuestion(id: Int) = Action {
		DerivativeQuestions.delete(id);
		Ok(views.html.self_quiz_list(DerivativeQuestions.all()))
	}

	// ======== Self Quiz Answers ======== 
	def selfQuizAnswer(qid: Int, aid: Int) = Action {
		val question = DerivativeQuestions.read(qid).get // TODO can be null
		val answer = DerivativeQuestionAnswers.read(qid, aid)
		Ok(views.html.self_quiz_answer(question, answer))
	}

	def answerSelfQuizQuestion = Action { implicit request =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.self_quiz_answer(DerivativeQuestions.read(errors.get._1).get, None)) // TODO currently we assume we can get the problem id here
			},
			answerForm => {
				val question = DerivativeQuestions.read(answerForm._1).get // TODO check for no question here
				val mathML = MathML(answerForm._2).get // TODO can fail here
				val rawStr = answerForm._3
				val synched = answerForm._4

				val answer = DerivativeQuestionAnswers.create(question, rawStr, mathML, synched);
				Redirect(routes.Application.selfQuizAnswer(question.id, answer.id))
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
