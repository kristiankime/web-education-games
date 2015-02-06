package controllers.quiz.tangent

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{TangentAnswers, TangentAnswerUnfinished}
import models.quiz.question.{TangentQuestion}
import models.support._
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
            val (slopeMathML, slopeRawStr, interceptMathML, interceptRawStr) = TangentAnswerForm(form)
            val unfinishedAnswer = TangentAnswerUnfinished(user.id, question.id, MathML(slopeMathML).get, slopeRawStr, MathML(interceptMathML).get, interceptRawStr, JodaUTC.now)_
            TangentAnswers.correct(question, slopeMathML, interceptMathML) match {
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
  val values = Form(tuple(slopeMathML -> text, slopeRawStr -> text, interceptMathML -> text, interceptRawStr -> text))

  def apply(v : (String, String, String, String)) = (MathML(v._1).get, v._2, MathML(v._3).get, v._4) // TODO better error handling for .get(s)
}