package controllers

import scala.slick.session.Session
import mathml.MathML
import models.question._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.table._

object DerivativeQuestionController extends Controller with SecureSocial {

	def selfQuiz = SecuredAction {
		Ok(views.html.self_quiz())
	}

	def questions = SecuredAction { implicit request =>
		Ok(views.html.self_quiz_questions(Questions.allQuestions()))
	}

	def question(id: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			val set = sid.flatMap(Quizes.findQuiz(_))
			val question = Questions.findQuestion(id).get // TODO better error if this is empty
			Ok(views.html.self_quiz_answer(question, None, set))
		}
	}

	def newQuestion = SecuredAction { implicit request =>
		request.user match {
			case user: User => {
				DerivativeQuestionHTML.form.bindFromRequest.fold(
					errors => BadRequest(views.html.self_quiz_questions(Questions.allQuestions())),
					form => {

						val mathML = MathML(form._1).get // TODO better error if this is empty
						Questions.createQuestion(user, mathML, form._2, form._3)
						Redirect(routes.DerivativeQuestionController.questions)
					})
			}
			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // did not get a User instance, log error throw exception 
		}

	}

	def deleteQuestion(id: Long) = SecuredAction { implicit request =>
		Questions.deleteQuestion(id);
		Ok(views.html.self_quiz_questions(Questions.allQuestions()))
	}

}

object DerivativeQuestionHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
