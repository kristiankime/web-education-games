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

	def update = DBAction { implicit dbSessionRequest =>
		QuestionSetEditHTML.form.bindFromRequest.fold(
			errors => Ok("TODO"),
			form => {
				val foo = form._1;
				val foo2 = form._2;
				
//				DerivativeQuestionSetsModel.update(DerivativeQuestionSet(form._1, form._2))
				
//				MathML(form._1).foreach(DerivativeQuestionsModel.create(_, form._2, form._3))
//				Redirect(routes.DerivativeQuestion.selfQuizQuestions)
				Ok("TODO")
			})
	}

}

object QuestionSetEditHTML {
	val id = "id"
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(id -> longNumber, name -> nonEmptyText, questionId -> list(boolean)))
}