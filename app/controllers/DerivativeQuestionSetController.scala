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

	def selfQuizQuestionSet(id: Long) = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_question_set(DerivativeQuestionSetsModel.read(id), DerivativeQuestionsModel.all))
	}

	def selfQuizQuestionSets = DBAction { implicit dbSessionRequest =>
		Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
	}

	def update = DBAction { implicit dbSessionRequest =>
		QuestionSetEditHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				DerivativeQuestionSetsModel.update(DerivativeQuestionSet(form._1, form._2))
				DerivativeQuestionSetLinksModel.update(form._1, form._3)
				Redirect(routes.DerivativeQuestionSetController.selfQuizQuestionSets)
			})
	}

	def create = DBAction { implicit dbSessionRequest =>
		QuestionSetCreateHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
			form => {
				val id = DerivativeQuestionSetsModel.create(form._1)
				DerivativeQuestionSetLinksModel.update(id, form._2)
				Redirect(routes.DerivativeQuestionSetController.selfQuizQuestionSets)
			})
	}

}

trait QuestionSetHTML {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
}

object QuestionSetEditHTML extends QuestionSetHTML {
	val id = "id"
	val form = Form(tuple(id -> longNumber, name -> nonEmptyText, questionId -> list(longNumber)))
}

object QuestionSetCreateHTML extends QuestionSetHTML {
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}