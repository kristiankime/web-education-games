package controllers.quiz.tangent

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.derivative.DerivativeAnswerForm
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{DerivativeAnswerUnfinished, DerivativeAnswers}
import models.quiz.question.DerivativeQuestion
import models.quiz.{Quiz, Quizzes}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait TangentAnswersControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : DerivativeQuestion)) => {
        DerivativeAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math : MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val quizOp : Option[Quiz] = Quizzes(quizId)
            val unfinishedAnswer = DerivativeAnswerUnfinished(user.id, question.id, math, rawStr, JodaUTC.now)_
            DerivativeAnswers.correct(question, math) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(DerivativeAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.derivative.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeQuestion.getClass.getSimpleName))
    }
  }

}

object TangentAnswerForm {
  val slopeMathML = "slopeMathML"
  val slopeRawStr = "slopeRawStr"
  val interceptMathML = "interceptMathML"
  val interceptRawStr = "interceptRawStr"
  val values = Form(tuple(slopeMathML -> text, slopeRawStr -> text, interceptMathML -> text, interceptRawStr -> text))
}