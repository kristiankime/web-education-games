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
import service.Own
import service.Access
import models.organization._
import mathml.Match._

object AnswersController extends Controller with SecureSocial {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)

		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)
		val answerOp = Answers.find(answerId)

		(courseOp, quizOp, questionOp, answerOp) match {
			case (Some(course), Some(quiz), Some(question), Some(answer)) => 
				questionView(Access(Courses.checkAccess(course.id)), Some(course), quiz, question, Some(Right(answer)))
			case (None, Some(quiz), Some(question), Some(answer)) => 
				questionView(Access(user, question.owner), None, quiz, question, Some(Right(answer)))
			case _ => BadRequest(views.html.index())
		}
	}

	private def questionView(access: Access, course: Option[Course], quiz: Quiz, question: Question, answer: Option[Either[AnswerTmp, Answer]])(implicit user: User) = {
		val userAnswers = Questions.findAnswers(question.id, user)
		val allAnswers = Questions.findAnswersAndOwners(question.id)
		Ok(views.html.question.derivative.questionView(access, course, quiz, question, answer, userAnswers, allAnswers))
	}

	def create(quizId: QuizId, questionId: QuestionId, courseId: Option[CourseId]) = SecuredAction { implicit request =>
		implicit val user = User(request)
		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val question = Questions.find(questionId).get // TODO check for no question here
				val mathML = MathML(form._1).get // TODO can fail here
				val rawStr = form._2
				
				val correct = Answers.correct(question, mathML)

				correct match {
					case Yes => {
						val answer = Answers.createAnswer(AnswerTmp(user.id, question.id, mathML, rawStr, true, DateTime.now))
						Redirect(routes.AnswersController.view(quizId, questionId, answer.id, courseId))
					}
					case No => {
						val answer = Answers.createAnswer(AnswerTmp(user.id, question.id, mathML, rawStr, false, DateTime.now))
						Redirect(routes.AnswersController.view(quizId, questionId, answer.id, courseId))
					}
					case Inconclusive => { // No state modification so we don't need to redirect
						val answerTmp = AnswerTmp(user.id, question.id, mathML, rawStr, false, DateTime.now)
						val courseOp = courseId.flatMap(Courses.find(_))
						val quiz = Quizzes.find(quizId).get // TODO can error
						
						val access = 
							if(courseOp.nonEmpty) { Access(Courses.checkAccess(courseOp.get.id)) }
							else { Access(user, question.owner) }
									
						questionView(access, courseOp, quiz, question, Some(Left(answerTmp)))
					}
				}
				
			})
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
