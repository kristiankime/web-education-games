package controllers.question.derivative

import scala.util._
import com.artclod.slick.JodaUTC
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.util._
import play.api.db.slick.Config.driver.simple._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.support.{SecureSocialConsented, SecureSocialDB}
import models.support._
import models.organization._
import models.question.derivative._
import service._


object AnswersController extends Controller with SecureSocialConsented {

  def apply(questionId: QuestionId, answerId: AnswerId)(implicit session: Session) : Either[Result, Answer] =
    Answers(answerId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no answer for id=["+answerId+"]")))
      case Some(answer) =>
        if(answer.id ^!= answerId) Left(NotFound(views.html.errors.notFoundPage("The answer id=["+answerId+"] was not for the question id=[" + questionId + "]")))
        else Right(answer)
    }

	def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId, answerId: AnswerId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
    QuestionsController(quizId, questionId) +
    AnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question, answer)) => questionView(course, quiz, question, Some(Right(answer)))
    }
	}

	def create(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question)) => {
        AnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math : MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val quizOp : Option[Quiz] = Quizzes(quizId)
            val unfinishedAnswer = UnfinishedAnswer(user.id, question.id, math, rawStr, JodaUTC.now)_
            Answers.correct(question, math) match {
              case Yes => Redirect(routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id))
              case No => Redirect(routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => questionView(course, quiz, question, Some(Left(unfinishedAnswer(false))))
            }
          })
      }
    }
	}

	private def questionView(course: Course, quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) = {
    Ok(views.html.question.derivative.questionView(course, quiz, question.results(user), answer))
	}
}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
