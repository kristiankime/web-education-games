package controllers.quiz.graphmatch

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{GraphMatchAnswerUnfinished, GraphMatchAnswers, DerivativeAnswerUnfinished, DerivativeAnswers}
import models.quiz.question.{GraphMatchQuestionWhich, GraphMatchQuestion, DerivativeQuestion}
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait GraphMatchAnswersControllon extends Controller with SecureSocialConsented {

  def createGraphMatch(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : GraphMatchQuestion)) => {
        GraphMatchAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = GraphMatchAnswerForm.toAnswerUnfinished(user, question, form)
            GraphMatchAnswers.correct(question, form.guessIndex) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(GraphMatchAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, GraphMatchAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.graphmatch.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + GraphMatchQuestion.getClass.getSimpleName))
    }
  }

}

object GraphMatchAnswerForm {
  val guessIndex = "guessIndex"

  val values = Form(mapping(guessIndex -> number (min=GraphMatchQuestionWhich.whichMin, max=GraphMatchQuestionWhich.whichMax))
    (GraphMatchAnswerForm.apply)(GraphMatchAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: GraphMatchQuestion, form: GraphMatchAnswerForm) = GraphMatchAnswerUnfinished(user.id, question.id, form.guessIndex, "", JodaUTC.now)_
}

case class GraphMatchAnswerForm(guessIndexInt: Int) {
  val guessIndex = guessIndexInt.toShort
}