package controllers.quiz.derivative

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{DerivativeQuestion, DerivativeQuestions, QuestionDifficulty}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

import scala.util.Left

trait DerivativeQuestionsControllon extends Controller with SecureSocialConsented {

  def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            DerivativeQuestions.create(DerivativeQuestion(null, user.id, mathML, form._2, JodaUTC.now, QuestionDifficulty(mathML)), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

}

object DerivativeQuestionForm {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val values = Form(tuple(mathML -> nonEmptyText, rawStr -> nonEmptyText))
}
