package controllers.question.derivative

import com.artclod.mathml.MathML
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.question.derivative._
import models.support._
import models.organization.Courses
import org.joda.time.DateTime
import service._
import controllers.support.SecureSocialDB

object QuestionsController extends Controller with SecureSocialDB {

	def view(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)

		(courseOp, quizOp, questionOp) match {
			case (Some(course), Some(quiz), Some(question)) => {
				val access =  course.access
				val nextQuestion = quiz.results(user).nextQuestion(question)
				val allAnswers = Questions.findAnswersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, Some(course), quiz, question.results(user), None, nextQuestion, allAnswers))
			}
			case (None, Some(quiz), Some(question)) => {
				val access =  Access(user, question.owner)
				val nextQuestion = quiz.results(user).nextQuestion(question)
				val allAnswers = Questions.findAnswersAndOwners(questionId)
				Ok(views.html.question.derivative.questionView(access, None, quiz, question.results(user), None, nextQuestion, allAnswers))
			}
			case _ => BadRequest(views.html.index(Courses.listDetails))
		}
	}

	def create(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Courses.listDetails)),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(QuestionTmp(user.id, mathML, form._2, DateTime.now), quizId)
				Redirect(routes.QuizzesController.view(quizId, courseId))
			})
	}

	def remove(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		(Quizzes.find(quizId), Questions.find(questionId)) match {
			case (Some(quiz), Some(question)) => {
				Questions.remove(quiz, question)
				Redirect(routes.QuizzesController.view(quizId, courseId))
			}
			case _ => BadRequest(views.html.index(Courses.listDetails))
		}
	}
}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
