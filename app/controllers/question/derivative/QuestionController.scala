package controllers.question.derivative

import scala.slick.session.Session
import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.table._
import service.User
import models.question.derivative._
import models.id._

object QuestionController extends Controller with SecureSocial {

	def selfQuiz = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.self_quiz())
	}

	def questions = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.self_quiz_questions(Questions.allQuestions()))
	}

	def questionsByUser(uid: UserId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Ok(views.html.self_quiz_questions(Questions.findQuestionsForUser(uid)))
	}

	def question(id: QuestionId, sid: Option[QuizId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		val set = sid.flatMap(q => Quizzes.findQuiz(q))
		val question = Questions.findQuestion(id).get // TODO better error if this is empty
		Ok(views.html.self_quiz_answer(question, None, set))
	}

	def newQuestion = SecuredAction { implicit request =>
		implicit val user = User(request)
		QuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_questions(Questions.allQuestions())),
			form => {

				val mathML = MathML(form._1).get // TODO better error if this is empty
				Questions.createQuestion(User(request), mathML, form._2, form._3)
				Redirect(routes.QuestionController.questions)
			})

	}

	def deleteQuestion(id: QuestionId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Questions.deleteQuestion(id);
		Ok(views.html.self_quiz_questions(Questions.allQuestions()))
	}

}

object QuestionHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
