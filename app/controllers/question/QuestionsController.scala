package controllers.question

import com.artclod.mathml.MathML
import com.artclod.util._
import controllers.question.derivative.DerivativeQuestionsControllon
import controllers.question.tangent.TangentQuestionsControllon
import controllers.support.SecureSocialConsented
import models.quiz.question.{QuestionScoring, QuestionDifficulty, DerivativeQuestion, DerivativeQuestions}
import models.support._
import play.api.db.slick.Config.driver.simple.Session
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller, Result}

import scala.util.{Failure, Success}

object QuestionsController extends Controller with SecureSocialConsented with DerivativeQuestionsControllon with TangentQuestionsControllon {

  def apply(quizId: QuizId, questionId: QuestionId)(implicit session: Session) : Either[Result, DerivativeQuestion] =
    DerivativeQuestions(questionId) match {
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
        Ok(views.html.question.questionView(course, quiz, question.results(user), None))
      }
    }
	}

	def remove(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
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
              val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
              val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
              Ok(Json.toJson(DifficultyResponse(difficultyRequest.rawStr, mathML.toString, diff, correct, incorrect)))
            }
          }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}
