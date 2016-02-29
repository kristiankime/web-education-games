package controllers.quiz.derivativegraph

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{DerivativeGraphAnswers, DerivativeGraphAnswerUnfinished, TangentAnswerUnfinished, TangentAnswers}
import models.quiz.question.{DerivativeGraphQuestion, TangentQuestion}
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support.DerivativeOrder.derivativeOrderFormatter

trait DerivativeGraphAnswersControllon extends Controller with SecureSocialConsented {

  def createDerivativeGraph(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : DerivativeGraphQuestion)) => {
        DerivativeGraphAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = DerivativeGraphAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeGraphAnswers.correct(question, form.derivativeOrder) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(DerivativeGraphAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, DerivativeGraphAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.derivativegraph.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeGraphQuestion.getClass.getSimpleName))
    }
  }

}

object DerivativeGraphAnswerForm {
  val derivativeOrder = "derivativeOrder"

  val values = Form(mapping(derivativeOrder -> of[DerivativeOrder])
    (DerivativeGraphAnswerForm.apply)(DerivativeGraphAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: DerivativeGraphQuestion, form: DerivativeGraphAnswerForm) = DerivativeGraphAnswerUnfinished(user.id, question.id, form.derivativeOrder, "", JodaUTC.now)_
}

case class DerivativeGraphAnswerForm(derivativeOrder: DerivativeOrder)