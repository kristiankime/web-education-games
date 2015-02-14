package controllers.quiz.tangent

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{TangentAnswerUnfinished, TangentAnswers}
import models.quiz.question.TangentQuestion
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait TangentAnswersControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : TangentQuestion)) => {
        TangentAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = TangentAnswerForm.toAnswerUnfinished(user, question, form)
            TangentAnswers.correct(question, form.slopeMathML, form.interceptMathML) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(TangentAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, TangentAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.tangent.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + TangentQuestion.getClass.getSimpleName))
    }
  }

}

object TangentAnswerForm {
  val slopeMathML = "slopeMathML"
  val slopeRawStr = "slopeRawStr"
  val interceptMathML = "interceptMathML"
  val interceptRawStr = "interceptRawStr"

  val values = Form(mapping(
    slopeMathML -> nonEmptyText,
    slopeRawStr -> nonEmptyText,
    interceptMathML -> nonEmptyText,
    interceptRawStr -> nonEmptyText)
    (TangentAnswerForm.apply)(TangentAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: TangentQuestion, form: TangentAnswerForm) = TangentAnswerUnfinished(user.id, question.id, form.slopeMathML, form.slopeRawStr, form.interceptMathML, form.interceptRawStr, JodaUTC.now)_
}

case class TangentAnswerForm(slope : String, slopeRawStr : String, intercept : String, interceptRawStr : String) {
  def slopeMathML = MathML(slope).get // TODO better error handling for .get(s)
  def interceptMathML = MathML(intercept).get
}