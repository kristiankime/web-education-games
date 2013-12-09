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

object DerivativeQuestionSetController extends Controller {

	def selfQuizQuestionSetAnswer(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionSetsModel.readQuestion(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set(s._1, s._2))
			case None => Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
		}
	}

	def selfQuizQuestionSetEdit(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionSetsModel.read(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_update(s, DerivativeQuestionsModel.all))
			case None => Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
		}
	}

	def selfQuizQuestionSets = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
	}

	def updateSelfQuizQuestionSet = DBAction { implicit dbSessionRequest =>
		QuestionSetEditHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				DerivativeQuestionSetsModel.update(DerivativeQuestionSet(form._1, form._2), form._3)
				Redirect(routes.DerivativeQuestionSetController.selfQuizQuestionSets)
			})
	}

	def newSelfQuizQuestionSet = DBAction { implicit dbSessionRequest =>
		QuestionSetCreateHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				val id = DerivativeQuestionSetsModel.create(form._1, form._2)
				Redirect(routes.DerivativeQuestionSetController.selfQuizQuestionSets)
			})
	}

}

object QuestionSetHTML {
	val id = "id"
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
}

object QuestionSetEditHTML {
	import QuestionSetHTML._
	val form = Form(tuple(id -> longNumber, name -> nonEmptyText, questionId -> list(longNumber)))
}

object QuestionSetCreateHTML {
	import QuestionSetHTML._
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}