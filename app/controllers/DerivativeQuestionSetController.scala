package controllers

import scala.slick.session.Session

import models.question.DerivativeQuestionSet
import models.question.DerivativeQuestionSetsModel
import models.question.DerivativeQuestionsModel
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.list
import play.api.data.Forms.longNumber
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.tuple
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial

object DerivativeQuestionSetController extends Controller with SecureSocial {

	def sets = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
		}
	}

	def setCreate = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
		}
	}

	def setEdit(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionSetsModel.readIds(id) match {
				case Some(s) => Ok(views.html.self_quiz_question_set_edit(s, DerivativeQuestionsModel.all))
				case None => Ok(views.html.self_quiz_question_set_create(DerivativeQuestionsModel.all))
			}
		}
	}

	def setAnswer(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionSetsModel.readQuestion(id) match {
				case Some(s) => Ok(views.html.self_quiz_question_set_answer(s._1, s._2))
				case None => Ok(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all))
			}
		}
	}

	def newSet = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			QuestionSetHTML.form.bindFromRequest.fold(
				errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
				form => {
					val id = DerivativeQuestionSetsModel.create(form._1, form._2)
					Redirect(routes.DerivativeQuestionSetController.sets)
				})
		}
	}

	def updateSet(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			QuestionSetHTML.form.bindFromRequest.fold(
				errors => BadRequest(views.html.self_quiz_question_sets(DerivativeQuestionSetsModel.all)),
				form => {
					DerivativeQuestionSetsModel.update(DerivativeQuestionSet(id, form._1), form._2)
					Redirect(routes.DerivativeQuestionSetController.sets)
				})
		}
	}

}

object QuestionSetHTML {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}