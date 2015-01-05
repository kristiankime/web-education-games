package controllers.question.derivative

import controllers.question.QuizzesController
import models.question.tangent.{TangentQuestions, TangentQuestion}
import models.question.{QuestionDifficulty, QuestionScoring}
import play.api.db.slick.Config.driver.simple.Session
import com.artclod.slick.JodaUTC
import com.artclod.mathml.MathML
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Result, Controller}
import controllers.support.{SecureSocialConsented, SecureSocialDB}
import models.question.derivative._
import models.support._
import models.organization.Course
import scala.util.{Failure, Success}

trait TangentQuestionsControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        TangentQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (function, functionStr, atPointX, atPointXStr) = form
            TangentQuestions.create(TangentQuestion(null, user.id, MathML(function).get, functionStr, MathML(atPointX).get, atPointXStr, JodaUTC.now), quizId) // TODO better handle on error for MathML().get
            Redirect(controllers.question.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

}

object TangentQuestionForm {
  val function = "function"
  val functionStr = "functionStr"
  val atPointX = "atPointX"
  val atPointXStr = "atPointXStr"
  val values = Form(tuple(function -> nonEmptyText, functionStr -> nonEmptyText, atPointX -> nonEmptyText, atPointXStr -> nonEmptyText))
}