package controllers.quiz.multiplefunction

import com.artclod.mathml.{Match, Yes, No, Inconclusive}
import com.artclod.slick.JodaUTC
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer._
import models.quiz.question.{MultipleFunctionQuestion, MultipleChoiceQuestion, DerivativeQuestion}
import models.support._
import models.user.User
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, Controller}
import securesocial.core.SecuredRequest
import com.artclod.util._

trait MultipleFunctionAnswersControllon extends Controller with SecureSocialConsented {

  def createMultipleFunction(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : MultipleFunctionQuestion)) => {
        MultipleFunctionAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = MultipleFunctionAnswerForm.toAnswerUnfinished(user, question, form, JodaUTC.now)
            val answerOptions = MultipleFunctionAnswers.answerOptions(question, form)
            Match.from(answerOptions.map(_.correctNum).max) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(MultipleFunctionAnswers.createAnswer(unfinishedAnswer(true), answerOptions).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, MultipleFunctionAnswers.createAnswer(unfinishedAnswer(false), answerOptions).id))
              case Inconclusive => Ok(views.html.quiz.multiplefunction.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + MultipleFunctionQuestion.getClass.getSimpleName))
    }
  }

}

object MultipleFunctionAnswerForm {
  val functionGuesses = "functionGuesses"
  val functionGuessesStr = "functionGuessesStr"

  val values = Form(mapping(
    functionGuesses -> list(text),
      functionGuessesStr -> list(text))
    (MultipleFunctionAnswerForm.apply)(MultipleFunctionAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: MultipleFunctionQuestion, form: MultipleFunctionAnswerForm, now: DateTime) = MultipleFunctionAnswerUnfinished(user.id, question.id, "", now, form.functionGuesses)_
}

case class MultipleFunctionAnswerForm(functionGuesses: List[String], functionGuessesStr: List[String])