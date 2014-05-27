package controllers.question.derivative

import scala.util._
import com.artclod.slick.Joda
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.support.SecureSocialDB
import models.support._
import models.organization._
import models.organization.assignment._
import models.question.derivative._
import service._
import models.question.derivative.table.AnswerTimesTable
import play.api.db.slick.Config.driver.simple._


object AnswersController extends Controller with SecureSocialDB {

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
    val courseOp = courseIdOp.flatMap(Courses(_))
		val quizOp = Quizzes(quizId)
		val questionOp = Questions(questionId)
		val answerOp = Answers(answerId)

		(quizOp, questionOp, answerOp) match {
			case (Some(quiz), Some(question), Some(answer)) => {
        questionView(access(question, courseOp), where, quiz, question, Some(Right(answer)))
      }
			case _ => BadRequest(views.html.index())
		}
	}

	def create(quizId: QuizId, quetionId: QuestionId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

		AnswerForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val questionOp : Option[Question] = Questions(quetionId)
				val mathOp : Try[MathMLElem] = MathML(form._1)
				val rawStr = form._2
				val quizOp : Option[Quiz] = Quizzes(quizId)
				val courseOp  = courseIdOp.flatMap(Courses(_))

				(questionOp, mathOp, quizOp) match {
					case (Some(question), Success(math), Some(quiz)) => {
            val aTmp = AnswerLater(user.id, question.id, math, rawStr, Joda.now)_
						Answers.correct(question, math) match {
							case Yes => Redirect(routes.AnswersController.view(quiz.id, question.id, Answers.createAnswer(aTmp(true)).id, courseIdOp, groupIdOp))
							case No => Redirect(routes.AnswersController.view(quiz.id, question.id, Answers.createAnswer(aTmp(false)).id, courseIdOp, groupIdOp))
							case Inconclusive => questionView(access(question, courseOp), where, quiz, question, Some(Left(aTmp(false))))
						}
					}
					case _ => BadRequest(views.html.index())
				}
			})
	}

	private def questionView(access: Access, where: Either[Course, Group], quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) = {
		val allAnswers = Questions.answersAndOwners(question.id)
		val nextQuestion = quiz.results(user).nextQuestion(question)
		Ok(views.html.question.derivative.questionView(access, where, quiz, question.results(user), answer, nextQuestion, allAnswers))
	}

	private def access(qu: Question, cOp: Option[Course])(implicit user: User, session: Session) = {
		val cAccess = Access(cOp.map(_.access))
		val qAccess = Access(user, qu.ownerId)
		Seq(cAccess, qAccess).max
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
