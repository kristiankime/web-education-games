package controllers

import scala.slick.session.Session

import mathml.MathML
import models.DerivativeQuestionSetsModel
import models.DerivativeQuestionsModel
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial

object DerivativeQuestionController extends Controller with SecureSocial {

	def selfQuiz = SecuredAction {
		Ok(views.html.self_quiz())
	}

	def questions = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			Ok(views.html.self_quiz_questions(DerivativeQuestionsModel.all()))
		}
	}

	def question(id: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			val set = sid.flatMap(DerivativeQuestionSetsModel.read(_))
			Ok(views.html.self_quiz_answer(DerivativeQuestionsModel.read(id).get, None, set)) // TODO can be null
		}
	}

	def newQuestion = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionHTML.form.bindFromRequest.fold(
				errors => BadRequest(views.html.self_quiz_questions(DerivativeQuestionsModel.all())),
				form => {
					MathML(form._1).foreach(DerivativeQuestionsModel.create(_, form._2, form._3))
					Redirect(routes.DerivativeQuestionController.questions)
				})
		}
	}

	def deleteQuestion(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionsModel.delete(id);
			Ok(views.html.self_quiz_questions(DerivativeQuestionsModel.all()))
		}
	}

}

object DerivativeQuestionHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
