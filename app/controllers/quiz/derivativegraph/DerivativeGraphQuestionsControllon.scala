package controllers.quiz.derivativegraph

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.tangent.{TangentQuestionForm, TangentAnswerForm}
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.quiz.question.support.DerivativeOrder
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.api.data.format.Formats._
import models.quiz.question.support.DerivativeOrder.derivativeOrderFormatter

import scala.util.{Success, Failure}

trait DerivativeGraphQuestionsControllon extends Controller with SecureSocialConsented {

  def createDerivativeGraph(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeGraphQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.derivativeGraph(errors))),
          form => {
            DerivativeGraphQuestions.create(DerivativeGraphQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class DerivativeGraphDifficultyRequest(functionStr: String, function: String, atPointXStr: String, atPointX: String, partnerSkill: Double)
  case class DerivativeGraphDifficultyResponse(functionStr: String, function: String, atPointXStr: String, atPointX: String, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatDerivativeGraphDifficultyRequest = Json.format[DerivativeGraphDifficultyRequest]
  implicit val formatDerivativeGraphDifficultyResponse = Json.format[DerivativeGraphDifficultyResponse]

  def derivativeGraphQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[DerivativeGraphDifficultyRequest]
        .map { difficultyRequest =>
        (MathML(difficultyRequest.function), MathML(difficultyRequest.atPointX)) match {
          case (Failure(e), _) => BadRequest("Could not parse function [" + difficultyRequest.function + "] as mathml\n" + e.getStackTraceString)
          case (_, Failure(e)) => BadRequest("Could not parse atPointX [" + difficultyRequest.atPointX + "] as mathml\n" + e.getStackTraceString)
          case (Success(function), Success(atPointX)) => {
            val diff = DerivativeGraphQuestionDifficulty(function)
            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
            Ok(Json.toJson(DerivativeGraphDifficultyResponse(difficultyRequest.functionStr, difficultyRequest.function, difficultyRequest.atPointXStr, difficultyRequest.atPointX, diff, correct, incorrect)))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object DerivativeGraphQuestionForm {
  // Field Names
  val function = "function"
  val functionStr = "functionStr"
  val derivativeOrder = "derivativeOrder"
  val rangeLow = "rangeLow"
  val rangeHigh = "rangeHigh"
  // Validation Check Names
  val rangeValid = "rangeValid"

  val values = Form(
    mapping(function -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr -> nonEmptyText,
            derivativeOrder -> of[DerivativeOrder],
            rangeLow -> of[Double],
            rangeHigh -> of[Double])
    (DerivativeGraphQuestionForm.apply)(DerivativeGraphQuestionForm.unapply)
    verifying(rangeValid, fields => verifyRangeValid(fields) )
  )

  def toQuestion(user: User, form: DerivativeGraphQuestionForm) = DerivativeGraphQuestion(null, user.id, form.functionMathML, form.functionStr, JodaUTC.now, form.derivativeOrder, DerivativeGraphQuestionDifficulty(form.functionMathML))

  def verifyRangeValid(f: DerivativeGraphQuestionForm) = f.rangeLow < f.rangeHigh
}

case class DerivativeGraphQuestionForm(function: String, functionStr : String, derivativeOrder: DerivativeOrder, rangeLow: Double, rangeHigh: Double) {
  // TODO handle errors for .get
  def functionMathML = MathML(function).get
}