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

object DerivativeQuestion extends Controller {

	def selfQuiz = selfQuizQuestions

	def selfQuizQuestions = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_questions(DerivativeQuestionsModel.all()))
	}

	def selfQuizQuestion(id: Long) = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_answer(DerivativeQuestionsModel.read(id).get, None)) // TODO can be null
	}

	def newSelfQuizQuestion = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_questions(DerivativeQuestionsModel.all())),
			form => {
				MathML(form._1).foreach(DerivativeQuestionsModel.create(_, form._2, form._3))
				Redirect(routes.DerivativeQuestion.selfQuizQuestions)
			})
	}

	def deleteSelfQuizQuestion(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionsModel.delete(id);
		Ok(views.html.self_quiz_questions(DerivativeQuestionsModel.all()))
	}

}

object DerivativeQuestionHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
