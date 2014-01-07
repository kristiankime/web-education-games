package controllers.question.derivative

import scala.slick.session.Session
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import models.question.derivative._
import service.User
import controllers.question.derivative._

object QuizController extends Controller with SecureSocial {

	def sets = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_sets(Quizzes.allQuizzes))
	}

	def setCreate = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
	}

	def setEdit(id: Long) = SecuredAction { implicit request =>
		Quizzes.findQuizAndQuestionIds(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_edit(s, Questions.allQuestions))
			case None => Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
		}
	}

	def setAnswer(id: Long) = SecuredAction { implicit request =>
		Quizzes.findQuizAndQuestions(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_answer(s._1, s._2))
			case None => Ok(views.html.self_quiz_question_sets(Quizzes.allQuizzes))
		}
	}

	def newSet = SecuredAction { implicit request =>
		request.user match {
			case user: User => {
				QuestionSetHTML.form.bindFromRequest.fold(
					errors => BadRequest(views.html.self_quiz_question_sets(Quizzes.allQuizzes)),
					form => {
						val id = Quizzes.createQuiz(user, form._1, form._2)
						Redirect(routes.QuizController.sets)
					})
			}
			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // did not get a User instance, log error throw exception 
		}
	}

	def updateSet(id: Long) = SecuredAction { implicit request =>
		QuestionSetHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(Quizzes.allQuizzes)),
			form => {
				Quizzes.updateQuiz(Quiz(id, form._1), form._2)
				Redirect(routes.QuizController.sets)
			})
	}

}

object QuestionSetHTML {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}