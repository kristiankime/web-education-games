package controllers

import scala.slick.session.Session
import mathml.MathML
import models.question.DerivativeQuestionSetsModel
import models.question.DerivativeQuestionsModel
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import models.security.UserTable

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
			val question = DerivativeQuestionsModel.read(id).get // TODO better error if this is empty
			Ok(views.html.self_quiz_answer(question, None, set))
		}
	}

	def newQuestion = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionHTML.form.bindFromRequest.fold(
				errors => BadRequest(views.html.self_quiz_questions(DerivativeQuestionsModel.all())),
				form => {
					val user = UserTable.findByIdentityId(request.user.identityId).get // TODO better error if this is empty
					val mathML = MathML(form._1).get // TODO better error if this is empty
					DerivativeQuestionsModel.create(user, mathML, form._2, form._3)
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
