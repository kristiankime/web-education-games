package controllers.question.derivative

import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.question.derivative._
import models.id._
import org.joda.time.DateTime
import models.organization.Courses
import service.Own
import service.Access

object AnswersController extends Controller with SecureSocial {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)

		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)
		val answerOp = Answers.find(answerId)

		val access = courseOp.flatMap(c => Courses.checkAccess(c.id)).getOrElse(Own) // TODO get access right

		(courseOp, quizOp, questionOp, answerOp) match {
			case (Some(course), Some(quiz), Some(question), Some(answer)) => {
				val access = Access(Courses.checkAccess(course.id))
				val userAnswers = Questions.findAnswers(questionId, user)
				val allAnswers = Questions.findAnswersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, Some(course), quiz, question, Some(answer), userAnswers, allAnswers))
			}
			case (None, Some(quiz), Some(question), Some(answer)) => {
				val access = Access(user, question.owner)
				val userAnswers = Questions.findAnswers(questionId, user)
				val allAnswers = Questions.findAnswersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, None, quiz, question, Some(answer), userAnswers, allAnswers))
			}
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val question = Questions.find(questionId).get // TODO check for no question here
				val mathML = MathML(form._1).get // TODO can fail here
				val rawStr = form._2
				val answer = Answers.createAnswer(AnswerTmp(user.id, question.id, mathML, rawStr, Answers.correct(question, mathML), DateTime.now))
				Redirect(routes.AnswersController.view(quizId, questionId, answer.id, courseId))
			})
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
