package controllers.quiz.polynomialzone

import com.artclod.math.Interval
import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{PolynomialZoneAnswerUnfinished, PolynomialZoneAnswers}
import models.quiz.question.PolynomialZoneQuestion
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

import scala.util.Try

trait PolynomialZoneAnswersControllon extends Controller with SecureSocialConsented {

  def createPolynomialZone(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : PolynomialZoneQuestion)) => {
        PolynomialZoneAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = PolynomialZoneAnswerForm.toAnswerUnfinished(user, question, form)
            PolynomialZoneAnswers.correct(question, form.intervals) match {
              case true => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(PolynomialZoneAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case false => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, PolynomialZoneAnswers.createAnswer(unfinishedAnswer(false)).id))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + PolynomialZoneQuestion.getClass.getSimpleName))
    }
  }

}

object PolynomialZoneAnswerForm {
  val intervals = "intervals"
  // Validation Check Names
  val intervalsValid = "intervalsValid"

  val values = Form(mapping(
    intervals -> nonEmptyText.verifying(e => Try(models.support.string2VectorInterval(e)).isSuccess))
    (PolynomialZoneAnswerForm.apply)(PolynomialZoneAnswerForm.unapply)
    verifying(intervalsValid, fields => verifyIntervalsValid( fields.intervals ))
  )

  def verifyIntervalsValid(v : Vector[Interval]) = !Interval.overlap(v)

  def toAnswerUnfinished(user: User, question: PolynomialZoneQuestion, form: PolynomialZoneAnswerForm) = PolynomialZoneAnswerUnfinished(user.id, question.id, form.intervals, "", JodaUTC.now)_
}

case class PolynomialZoneAnswerForm(intervalsStr : String) {
  // TODO better error handling for .get(s)
  def intervals = models.support.string2VectorInterval(intervalsStr)
}