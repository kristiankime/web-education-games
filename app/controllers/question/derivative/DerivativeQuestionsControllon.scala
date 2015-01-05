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

trait DerivativeQuestionsControllon extends Controller with SecureSocialConsented {

	def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            DerivativeQuestions.create(DerivativeQuestion(null, user.id, mathML, form._2, JodaUTC.now), quizId)
            Redirect(controllers.question.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
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
