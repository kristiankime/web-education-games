package controllers.question.derivative

import org.joda.time.DateTime
import com.artclod.mathml.MathML
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import service._
import controllers.support.SecureSocialDB
import models.question.derivative._
import models.support._
import models.organization.Courses
import models.organization.assignment.Groups
import models.question.derivative.QuestionTmp

object QuestionsController extends Controller with SecureSocialDB {

	def view(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseId.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
		val courseOp = courseId.flatMap(Courses(_))
		val quizOp = Quizzes(quizId)
		val questionOp = Questions(questionId)

		(courseOp, quizOp, questionOp) match {
			case (Some(course), Some(quiz), Some(question)) => {
				val access =  course.access
				val nextQuestion = quiz.results(user).nextQuestion(question)
				val allAnswers = Questions.answersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, where, quiz, question.results(user), None, nextQuestion, allAnswers))
			}
			case (None, Some(quiz), Some(question)) => {
				val access =  Access(user, question.owner)
				val nextQuestion = quiz.results(user).nextQuestion(question)
				val allAnswers = Questions.answersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, where, quiz, question.results(user), None, nextQuestion, allAnswers))
			}
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, courseId: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(QuestionTmp(user.id, mathML, form._2, DateTime.now), quizId)
				Redirect(routes.QuizzesController.view(quizId, courseId, groupIdOp))
			})
	}

	def remove(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		(Quizzes(quizId), Questions(questionId)) match {
			case (Some(quiz), Some(question)) => {
				Questions.remove(quiz, question)
				Redirect(routes.QuizzesController.view(quizId, courseId, groupIdOp))
			}
			case _ => BadRequest(views.html.index())
		}
	}
}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
