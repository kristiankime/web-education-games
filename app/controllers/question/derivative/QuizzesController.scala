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

object QuizzesController extends Controller with SecureSocial {
	def list = TODO

	def add(courseId: Option[CourseId]) = TODO

	def create = TODO

	def view(quizId: QuizId) = TODO

	def update(quizId: QuizId) = TODO

	def delete(quizId: QuizId) = TODO
}

object QuizForm {
	val name = "name"
	val questionId = "questionId"
	def questionId(i: Int) = "questionId[" + i + "]"
	val form = Form(tuple(name -> nonEmptyText, questionId -> list(longNumber)))
}