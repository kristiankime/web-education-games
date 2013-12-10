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

	// Retrieve
	def sets = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
	}

	def setCreate = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
	}

	def setEdit(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionSetsModel.readIds(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_edit(s, DerivativeQuestionsModel.all))
			case None => Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
		}
	}

	def setAnswer(id: Long) = DBAction { implicit dbSessionRequest =>
		DerivativeQuestionSetsModel.readQuestion(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_answer(s._1, s._2))
			case None => Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
		}
	}

	// Create / Update
	def newSet = DBAction { implicit dbSessionRequest =>
		QuestionSetHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				val id = DerivativeQuestionSetsModel.create(form._1, form._2)
				Redirect(routes.DerivativeQuestionSetController.sets)
			})
	}

	def updateSet(id: Long) = DBAction { implicit dbSessionRequest =>
		QuestionSetHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				DerivativeQuestionSetsModel.update(DerivativeQuestionSet(id, form._1), form._2)
				Redirect(routes.DerivativeQuestionSetController.sets)
			})
	}

}

object QuestionSetHTML {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}