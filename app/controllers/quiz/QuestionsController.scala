package controllers.quiz

import com.artclod.mathml.MathML
import com.artclod.util._
import controllers.quiz.derivative.DerivativeQuestionsControllon
import controllers.quiz.derivativegraph.DerivativeGraphQuestionsControllon
import controllers.quiz.graphmatch.GraphMatchQuestionsControllon
import controllers.quiz.multiplechoice.MultipleChoiceQuestionsControllon
import controllers.quiz.multiplefunction.MultipleFunctionQuestionsControllon
import controllers.quiz.polynomialzone.PolynomialZoneQuestionsControllon
import controllers.quiz.tangent.TangentQuestionsControllon
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.support._
import play.api.db.slick.Config.driver.simple.Session
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller, Result}

import scala.util.{Failure, Success}

object QuestionsController extends Controller with SecureSocialConsented
  with DerivativeQuestionsControllon
  with DerivativeGraphQuestionsControllon
  with TangentQuestionsControllon
  with GraphMatchQuestionsControllon
  with PolynomialZoneQuestionsControllon
  with MultipleChoiceQuestionsControllon
  with MultipleFunctionQuestionsControllon {

  def apply(questionId: QuestionId)(implicit session: Session) : Either[Result, Question] =
    Questions(questionId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no question for id=["+questionId+"]")))
      case Some(question) => Right(question)
    }

  def apply(quizId: QuizId, questionId: QuestionId)(implicit session: Session) : Either[Result, Question] =
    Questions(questionId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no question for id=["+questionId+"]")))
      case Some(question) => question.quiz(quizId) match {
        case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for question for id=["+questionId+"]")))
        case Some(quiz) =>
          if(quiz.id ^!= quizId) { Left(NotFound(views.html.errors.notFoundPage("Question for id=["+questionId+"] is associated with quiz id=[" + quiz.id + "] not quiz id=[" + quizId + "]"))) }
          else { Right(question) }
      }
    }

	def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course , quiz, question)) => question match {
          case derivative : DerivativeQuestion => Ok(views.html.quiz.derivative.questionView(course, quiz, derivative.results(user), None))
          case derivativeGraph : DerivativeGraphQuestion => Ok(views.html.quiz.derivativegraph.questionView(course, quiz, derivativeGraph.results(user), None))
          case tangent : TangentQuestion => Ok(views.html.quiz.tangent.questionView(course, quiz, tangent.results(user), None))
          case graphMatch : GraphMatchQuestion => Ok(views.html.quiz.graphmatch.questionView(course, quiz, graphMatch.results(user), None))
          case polyZone : PolynomialZoneQuestion => Ok(views.html.quiz.polynomialzone.questionView(course, quiz, polyZone.results(user), None))
          case multipleChoice : MultipleChoiceQuestion => Ok(views.html.quiz.multiplechoice.questionView(course, quiz, multipleChoice.results(user), None))
          case multipleFunction : MultipleFunctionQuestion => Ok(views.html.quiz.multiplefunction.questionView(course, quiz, multipleFunction.results(user), None))
        }
    }
	}

	def remove(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
      QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question)) => {
        quiz.remove(question)
        Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
      }
    }
	}

  // These fields are for the ajax requests for questions scores
  val difficulty = "difficulty"
  val partnerSkill = "partnerSkill"
  val correctPoints = "correctPoints"
  val incorrectPoints = "incorrectPoints"
}
