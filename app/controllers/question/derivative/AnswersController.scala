package controllers.question.derivative

import mathml.MathML
import mathml.Match._
import models.id._
import models.question._
import models.question.derivative._
import models.organization._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.Own
import service.User
import service.Access
import scala.util._
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import controllers.support.SecureSocialDB
import scala.slick.session.Session

object AnswersController extends Controller with SecureSocialDB {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		val courseOp = courseId.flatMap(Courses.find(_))
		val quizOp = Quizzes.find(quizId)
		val questionOp = Questions.find(questionId)
		val answerOp = Answers.find(answerId)

		(quizOp, questionOp, answerOp) match {
			case (Some(qz), Some(qu), Some(a)) => questionView(access(qu, courseOp), courseOp, qz, qu, Some(Right(a)))
			case _ => BadRequest(views.html.index())
		}
	}

	def create(qzId: QuizId, quId: QuestionId, cId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val questionOp = Questions.find(quId)
				val mathOp = MathML(form._1)
				val rawStr = form._2
				val quizOp = Quizzes.find(qzId)
				val courseOp = cId.flatMap(Courses.find(_))

				(questionOp, mathOp, quizOp) match {
					case (Some(qu), Success(m), Some(qz)) => {
						val aTmp = AnswerTmp(user.id, qu.id, m, rawStr, now)_
						Answers.correct(qu, m) match {
							case Yes => Redirect(routes.AnswersController.view(qz.id, qu.id, Answers.createAnswer(aTmp(true)).id, cId))
							case No => Redirect(routes.AnswersController.view(qz.id, qu.id, Answers.createAnswer(aTmp(false)).id, cId))
							case Inconclusive => questionView(access(qu, courseOp), courseOp, qz, qu, Some(Left(aTmp(false))))
						}
					}
					case _ => BadRequest(views.html.index())
				}
			})
	}

	private def questionView(access: Access, course: Option[Course], quiz: Quiz, question: Question, answer: Option[Either[AnswerTmp, Answer]])(implicit user: User, session: Session) = {
		val userAnswers = Questions.findAnswers(question.id, user)
		val allAnswers = Questions.findAnswersAndOwners(question.id)
		Ok(views.html.question.derivative.questionView(access, course, quiz, question, answer, userAnswers, allAnswers))
	}

	private def access(qu: Question, cOp: Option[Course])(implicit user: User, session: Session) = {
		val cAccess = Access(cOp.flatMap(c => Courses.checkAccess(c.id)))
		val qAccess = Access(user, qu.owner)
		Access.better(cAccess, qAccess)
	}
}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
