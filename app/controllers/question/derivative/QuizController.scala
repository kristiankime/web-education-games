package controllers.question.derivative

import scala.slick.session.Session
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import models.question.derivative._
import service.User
import models.id._

object QuizController extends Controller with SecureSocial {

	def sets = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_sets(Quizzes.allQuizzes))
	}

	def setCreate = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
	}

	def setEdit(id: QuizId) = SecuredAction { implicit request =>
		Quizzes.findQuizAndQuestionIds(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_edit(s, Questions.allQuestions))
			case None => Ok(views.html.self_quiz_question_set_create(Questions.allQuestions))
		}
	}

	def setAnswer(id: QuizId) = SecuredAction { implicit request =>
		Quizzes.findQuizAndQuestions(id) match {
			case Some(s) => Ok(views.html.self_quiz_question_set_answer(s._1, s._2))
			case None => Ok(views.html.self_quiz_question_sets(Quizzes.allQuizzes))
		}
	}

	def newSet = SecuredAction { implicit request =>
		QuizHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(Quizzes.allQuizzes)),
			form => {
				val id = Quizzes.createQuiz(User(request), form._1, form._2.map(QuestionId(_)))
				Redirect(routes.QuizController.sets)
			})
	}

	def updateSet(id: QuizId) = SecuredAction { implicit request =>
		QuizHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_question_sets(Quizzes.allQuizzes)),
			form => {
				Quizzes.updateQuiz(Quiz(id, form._1), form._2.map(QuestionId(_)))
				Redirect(routes.QuizController.sets)
			})
	}

}

object QuizHTML {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}