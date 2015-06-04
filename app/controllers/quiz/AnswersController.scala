package controllers.quiz

import com.artclod.util._
import controllers.quiz.derivative.DerivativeAnswersControllon
import controllers.quiz.derivativegraph.DerivativeGraphAnswersControllon
import controllers.quiz.tangent.TangentAnswersControllon
import controllers.support.SecureSocialConsented
import models.quiz.answer._
import models.quiz.question.{DerivativeQuestion, TangentQuestion}
import models.support._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc.{Controller, Result}

import scala.util._

object AnswersController extends Controller with SecureSocialConsented
  with DerivativeAnswersControllon
  with DerivativeGraphAnswersControllon
  with TangentAnswersControllon {

  def apply(questionId: QuestionId, answerId: AnswerId)(implicit session: Session) : Either[Result, Answer] =
    Answers(answerId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no answer for id=["+answerId+"]")))
      case Some(answer) =>
        if(answer.id ^!= answerId) Left(NotFound(views.html.errors.notFoundPage("The answer id=["+answerId+"] was not for the question id=[" + questionId + "]")))
        else Right(answer)
    }

	def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId, answerId: AnswerId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) + AnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question, answer)) => (question, answer) match {
        case (dq: DerivativeQuestion, da: DerivativeAnswer) => Ok(views.html.quiz.derivative.questionView(course, quiz, dq.results(user), Some(Right(da))))
        case (tq: TangentQuestion, ta: TangentAnswer) => Ok(views.html.quiz.tangent.questionView(course, quiz, tq.results(user), Some(Right(ta))))
        case _ => Ok(views.html.errors.notFoundPage("Question " + questionId + " type did not match Answer " + answerId))
      }
    }
  }

}


