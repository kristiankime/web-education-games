package controllers

import scala.slick.session.Session

import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial

object DerivativeQuestionSetController extends Controller with SecureSocial {

	def sets = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_sets(Quizes.allQuizes))
	}

	def setCreate = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
	}

	def setEdit(id: Long) = SecuredAction { implicit request =>
		Quizes.findQuizAndQuestionIds(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_edit(s, Questions.allQuestions))
			case None => Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
		}
	}

	def setAnswer(id: Long) = SecuredAction { implicit request =>
		Quizes.findQuizAndQuestions(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_answer(s._1, s._2))
			case None => Ok(views.html.self_quiz_question_sets(Quizes.allQuizes))
		}
	}

	def newSet = SecuredAction { implicit request =>
		QuestionSetHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(Quizes.allQuizes)),
			form => {
				val id = Quizes.createQuiz(form._1, form._2)
				Redirect(routes.DerivativeQuestionSetController.sets)
			})
	}

	def updateSet(id: Long) = SecuredAction { implicit request =>
		QuestionSetHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(Quizes.allQuizes)),
			form => {
				Quizes.updateQuiz(Quiz(id, form._1), form._2)
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