package controllers.game

import com.artclod.util._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.game.GamesRequesteeController._
import controllers.question.derivative.QuestionsController
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import models.question.derivative.{Quiz, Question, Questions}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import play.api.db.slick.Config.driver.simple.Session
import service.User

import scala.util.Right

object GamesRequestorController extends Controller with SecureSocialConsented {

  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => game.requestorQuiz match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] is does not have a Requestee Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  def create(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) =>
        GameRequestorCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (updatedGame, quiz) = game.ensureRequestorQuiz
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
        GameRequestorRemove.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = game.ensureRequestorQuiz
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
          case g : RequestorQuiz => g
          case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[RequestorQuiz].getName + "] but was " + game.toState)
        }
        Games.update(gameState.finalizeRequestorQuiz)

        Redirect(routes.GamesController.game(organization.id, course.id, game.id))
      }
    }
  }

  def answeringDone(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) => {

        val gameState = game.toState match {
          case g : RequesteeQuizFinished with RequestorStillAnswering => g
          case _ =>  throw new IllegalStateException("State should have been subclass of RequesteeQuizFinished with RequestorStillAnswering but was " + game.toState)
        }
        Games.update(gameState.requestorDoneAnswering)

        Redirect(routes.GamesController.game(organization.id, course.id, game.id))
      }
    }
  }

}

object GameRequestorRemove {
  val removeId = "removeId"
  val form = Form(removeId -> questionId)
}

object GameRequestorCreate {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val form = Form(tuple(mathML -> text, rawStr -> text))
}
