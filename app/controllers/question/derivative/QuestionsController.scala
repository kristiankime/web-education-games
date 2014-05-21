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

object QuestionsController extends Controller with SecureSocialDB {

	def view(quizId: QuizId, questionId: QuestionId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		(Quizzes(quizId), Questions(questionId)) match {
			case (Some(quiz), Some(question)) => {
        val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
				val nextQuestion = quiz.results(user).nextQuestion(question)
				val allAnswers = Questions.answersAndOwners(questionId)
        Answers.startWorkingOn(question.id)
        Ok(views.html.question.derivative.questionView(quiz.access, where, quiz, question.results(user), None, nextQuestion, allAnswers))
			}
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.errors.formErrorPage(errors)),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(Question(null, user.id, mathML, form._2, DateTime.now), quizId)
				Redirect(routes.QuizzesController.view(quizId, courseIdOp, groupIdOp))
			})
	}

	def remove(quizId: QuizId, questionId: QuestionId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		(Quizzes(quizId), Questions(questionId)) match {
			case (Some(quiz), Some(question)) => {
        quiz.remove(question)
				Redirect(routes.QuizzesController.view(quizId, courseIdOp, groupIdOp))
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
