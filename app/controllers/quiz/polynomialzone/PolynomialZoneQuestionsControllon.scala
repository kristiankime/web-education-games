package controllers.quiz.polynomialzone

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.{QuestionForms, QuizzesController}
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.quiz.question.support.PolynomialZoneType
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AnyContent, Action, Controller}
import views.html.mathml.correct
import play.api.data.format.Formats._
import com.artclod.play.shortFormatter

import scala.util.{Try, Success, Failure}

trait PolynomialZoneQuestionsControllon extends Controller with SecureSocialConsented {

  def createPolynomialZone(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        PolynomialZoneQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.polynomialZone(errors))),
          form => {
            PolynomialZoneQuestions.create(PolynomialZoneQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class PolynomialZoneDifficultyRequest(scale: Double, rootsStr : Array[Int], zoneTypeShort: Short, partnerSkill: Double)
  case class PolynomialZoneDifficultyResponse(scale: Double, rootsStr : Array[Int], zoneTypeShort: Short, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatPolynomialZoneDifficultyRequest = Json.format[PolynomialZoneDifficultyRequest]
  implicit val formatPolynomialZoneDifficultyResponse = Json.format[PolynomialZoneDifficultyResponse]

  def polynomialZoneQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[PolynomialZoneDifficultyRequest]
        .map { difficultyRequest =>
          Try(PolynomialZoneType(difficultyRequest.zoneTypeShort)) match {
            case (Failure(e)) => BadRequest("Could not parse number [" + difficultyRequest.zoneTypeShort + "] as " + PolynomialZoneType.getClass.getSimpleName + "\n" + e.getStackTraceString)
            case (Success(zoneType)) => {
              val diff = PolynomialZoneQuestionDifficulty(difficultyRequest.rootsStr.toVector, difficultyRequest.scale, zoneType)
              val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
              val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
              Ok(Json.toJson(PolynomialZoneDifficultyResponse(difficultyRequest.scale, difficultyRequest.rootsStr, difficultyRequest.zoneTypeShort, diff, correct, incorrect)))
            }
          }
        }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object PolynomialZoneQuestionForm {
  val roots = "roots"
  val scale = "scale"
  val zoneType = "zoneType"

  val values = Form(
    mapping(roots -> nonEmptyText.verifying(e => strToRoots(e).isSuccess),
            scale -> of[Double],
            zoneType -> of[Short].verifying(e => Try(PolynomialZoneType(e)).isSuccess))
    (PolynomialZoneQuestionForm.apply)(PolynomialZoneQuestionForm.unapply)
  )

  def strToRoots(str: String) =  Try(str.split(",").map(e => e.toInt).toVector)

  def toQuestion(user: User, form: PolynomialZoneQuestionForm) = PolynomialZoneQuestion(null, user.id, form.roots, form.scale, form.zoneType, JodaUTC.now, PolynomialZoneQuestionDifficulty(form.roots, form.scale, form.zoneType))

}

case class PolynomialZoneQuestionForm(rootsStr : String, scale: Double, zoneTypeShort: Short) {
  // TODO handle errors for .get
  def roots : Vector[Int] = PolynomialZoneQuestionForm.strToRoots(rootsStr).get
  def zoneType : PolynomialZoneType = PolynomialZoneType(zoneTypeShort)
}