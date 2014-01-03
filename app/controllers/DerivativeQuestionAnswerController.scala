package controllers

import scala.slick.session.Session
import mathml.MathML
import models.question.DerivativeQuestionAnswersModel
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
import models.question.DerivativeQuestionAnswersModel

object DerivativeQuestionAnswerController extends Controller with SecureSocial {

	def answers(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			val question = DerivativeQuestionsModel.read(id).get // TODO can be null
			Ok(views.html.self_quiz_question_answers(question, DerivativeQuestionAnswersModel.read(id)))
		}
	}

	def answer(qid: Long, aid: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			val question = DerivativeQuestionsModel.read(qid).get // TODO can be null
			val answer = DerivativeQuestionAnswersModel.read(qid, aid)
			val set = sid.flatMap(DerivativeQuestionSetsModel.read(_))
			Ok(views.html.self_quiz_answer(question, answer, set))
		}
	}

	def newAnswer(qid: Long, sid: Option[Long]) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
				errors => {
					BadRequest(views.html.self_quiz_answer(DerivativeQuestionsModel.read(qid).get, None, None)) // TODO currently we assume we can get the problem id here
				},
				answerForm => {
					val question = DerivativeQuestionsModel.read(qid).get // TODO check for no question here
					val mathML = MathML(answerForm._1).get // TODO can fail here
					val rawStr = answerForm._2
					val synched = answerForm._3

					val answerId = DerivativeQuestionAnswersModel.create(question, rawStr, mathML, synched)
					Redirect(routes.DerivativeQuestionAnswerController.answer(question.id, answerId, sid))
				})
		}
	}

}

object DerivativeQuestionAnswerHTML {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val form = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
