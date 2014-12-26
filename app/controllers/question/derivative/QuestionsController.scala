package controllers.question.derivative

import models.question.QuestionScore
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

object QuestionsController extends Controller with SecureSocialConsented {

  def apply(quizId: QuizId, questionId: QuestionId)(implicit session: Session) : Either[Result, Question] =
    Questions(questionId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no question for id=["+questionId+"]")))
      case Some(question) => question.quiz match {
        case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for question for id=["+questionId+"]")))
        case Some(quiz) =>
          if(quiz.id ^!= quizId) {
            Left(NotFound(views.html.errors.notFoundPage("Question for id=["+questionId+"] is associated with quiz id=[" + quiz.id + "] not quiz id=[" + quizId + "]")))
          }
          else Right(question)
      }
    }

	def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course , quiz, question)) => {
        val nextQuestion = quiz.results(user).nextQuestion(question)
        Answers.startWorkingOn(question.id)
        Ok(views.html.question.derivative.questionView(course, quiz, question.results(user), None))
      }
    }
	}

	def create(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        QuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quizId)
            Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
	}

	def remove(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
      QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question)) => {
        quiz.remove(question)
        Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
      }
    }
	}


  // Meta Information for difficulty ajax call
  val mathML = "mathML"
  val rawStr = "rawStr"
  val difficulty = "difficulty"
  val partnerSkill = "partnerSkill"
  val correctPoints = "correctPoints"
  val incorrectPoints = "incorrectPoints"
  case class DifficultyRequest(rawStr: String, mathML: String, partnerSkill: Double)
  case class DifficultyResponse(rawStr: String, mathML: String, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatDifficultyRequest = Json.format[DifficultyRequest]
  implicit val formatDifficultyResponse = Json.format[DifficultyResponse]

  def questionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[DifficultyRequest]
        .map { difficultyRequest =>
          MathML(difficultyRequest.mathML) match {
            case Failure(e) => BadRequest("Could not parse [" + difficultyRequest.mathML + "] as mathml\n" + e.getStackTraceString)
            case Success(mathML) => {
              val diff = QuestionDifficulty(mathML)
              val correct = QuestionScore.teacherScore(diff, true, difficultyRequest.partnerSkill)
              val incorrect = QuestionScore.teacherScore(diff, false, difficultyRequest.partnerSkill)
              Ok(Json.toJson(DifficultyResponse(difficultyRequest.rawStr, mathML.toString, diff, correct, incorrect)))
            }
          }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}

