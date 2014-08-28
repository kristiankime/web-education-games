package controllers.game

import com.artclod.util._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.game.GamesController._
import controllers.organization.CoursesController
import controllers.question.derivative.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.game._
import models.organization.{Course, Organization}
import models.question.derivative.{Quiz, Question, Questions}
import models.support.{GameId, CourseId, OrganizationId}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import service.User
import play.api.db.slick.Config.driver.simple.Session
import models.support._

import scala.util.Right

object GamesRequesteeController extends Controller with SecureSocialConsented {

  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => game.requesteeQuiz match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] is does not have a Requestee Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  def create(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) =>
        GameRequesteeCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (updatedGame, quiz) = game.ensureRequesteeQuiz
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quiz.id)
            Redirect(routes.GamesController.game(organization.id, course.id, game.id))
          })
    }
  }

  def remove(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) =>
        GameRequesteeRemove.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = game.ensureRequesteeQuiz
            for(question <- Questions(questionId)) { quiz.remove(question) }
            Redirect(routes.GamesController.game(organization.id, course.id, game.id))
          })
    }
  }

  def quizDone(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) => {

        val gameState = game.toState match {
          case g : RequesteeQuiz => g
          case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[RequesteeQuiz].getName + "] but was " + game.toState)
        }
        Games.update(gameState.finalizeRequesteeQuiz)

        Redirect(routes.GamesController.game(organization.id, course.id, game.id))
      }
    }
  }


  def answeringDone(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) => {

        val gameState = game.toState match {
          case g : RequestorQuizFinished with RequesteeStillAnswering => g
          case _ =>  throw new IllegalStateException("State should have been subclass of RequestorQuizFinished with RequesteeStillAnswering but was " + game.toState)
        }
        Games.update(gameState.requesteeDoneAnswering)

        Redirect(routes.GamesController.game(organization.id, course.id, game.id))
      }
    }
  }
 }

object GameRequesteeRemove {
  val removeId = "removeId"
  val form = Form(removeId -> questionId)
}

object GameRequesteeCreate {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val form = Form(tuple(mathML -> text, rawStr -> text))
}