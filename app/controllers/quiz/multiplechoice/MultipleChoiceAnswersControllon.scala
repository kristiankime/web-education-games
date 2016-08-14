package controllers.quiz.multiplechoice

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer._
import models.quiz.question.{MultipleChoiceQuestion, DerivativeQuestion}
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait MultipleChoiceAnswersControllon extends Controller with SecureSocialConsented {

  def createMultipleChoice(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : MultipleChoiceQuestion)) => {
        MultipleChoiceAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = MultipleChoiceAnswerForm.toAnswerUnfinished(user, question, form)
            MultipleChoiceAnswers.correct(question, form.guessIndex) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(MultipleChoiceAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, MultipleChoiceAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.multiplechoice.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + MultipleChoiceQuestion.getClass.getSimpleName))
    }
  }

}

object MultipleChoiceAnswerForm {
  val guessIndex = "guessIndex"

  val values = Form(mapping(guessIndex -> number)
    (MultipleChoiceAnswerForm.apply)(MultipleChoiceAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: MultipleChoiceQuestion, form: MultipleChoiceAnswerForm) = MultipleChoiceAnswerUnfinished(user.id, question.id, form.guessIndex, "", JodaUTC.now)_
}

case class MultipleChoiceAnswerForm(guessIndexInt: Int) {
  val guessIndex = guessIndexInt.toShort
}