package controllers.question.derivative

import scala.util._
import com.artclod.slick.Joda
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.util._
import play.api.db.slick.Config.driver.simple._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.support.SecureSocialDB
import models.support._
import models.organization._
import models.question.derivative._
import service._


object AnswersController extends Controller with SecureSocialDB {

  def apply(questionId: QuestionId, answerId: AnswerId)(implicit session: Session) : Either[Result, Answer] =
    Answers(answerId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no answer for id=["+answerId+"]")))
      case Some(answer) =>
        if(answer.id ^!= answerId) Left(NotFound(views.html.errors.notFoundPage("The answer id=["+answerId+"] was not for the question id=[" + questionId + "]")))
        else Right(answer)
    }

	def view(quizId: QuizId, questionId: QuestionId, answerId: AnswerId, courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) +
    QuestionsController(quizId, questionId) +
    AnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz, question, answer)) => {
        questionView(access(question, course), course, quiz, question, Some(Right(answer)))
      }
    }
	}

	def create(quizId: QuizId, questionId: QuestionId, courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz, question)) => {
        AnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.index()),
          form => {
            val math : MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val quizOp : Option[Quiz] = Quizzes(quizId)
            val unfinishedAnswer = UnfinishedAnswer(user.id, question.id, math, rawStr, Joda.now)_
            Answers.correct(question, math) match {
              case Yes => Redirect(routes.AnswersController.view(quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id, course.id))
              case No => Redirect(routes.AnswersController.view(quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(false)).id, course.id))
              case Inconclusive => questionView(access(question, course), course, quiz, question, Some(Left(unfinishedAnswer(false))))
            }
          })
      }
    }
	}

	private def questionView(access: Access, course: Course, quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) = {
		val nextQuestion = quiz.results(user).nextQuestion(question)
		Ok(views.html.question.derivative.questionView(access, course, quiz, question.results(user), answer, nextQuestion))
	}

	private def access(qu: Question, cOp: Course)(implicit user: User, session: Session) = {
		val cAccess = cOp.access
		val qAccess = Access(user, qu.ownerId)
		Seq(cAccess, qAccess).max
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
