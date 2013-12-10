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

object DerivativeQuestionController extends Controller {

	def selfQuiz =  Action { 
		Ok(views.html.self_quiz())
	}

	def questions = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_questions(DerivativeQuestionsModel.all()))
	}

	def question(id: Long, sid: Option[Long]) = DBAction { implicit dbSessionRequest =>
		val set = sid.flatMap( DerivativeQuestionSetsModel.read(_) )
		Ok(views.html.self_quiz_answer(DerivativeQuestionsModel.read(id).get, None, set)) // TODO can be null
	}

	def newQuestion = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_questions(DerivativeQuestionsModel.all())),
			form => {
				MathML(form._1).foreach(DerivativeQuestionsModel.create(_, form._2, form._3))
				Redirect(routes.DerivativeQuestionController.questions)
			})
	}

	def deleteQuestion(id: Long) = DBAction { implicit dbSessionRequest =>
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
