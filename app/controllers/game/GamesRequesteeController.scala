package controllers.game

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.support.SecureSocialConsented
import models.game._
import models.question.derivative.{Question, Questions}
import models.support.{GameId, CourseId, OrganizationId}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import service.User
import play.api.db.slick.Config.driver.simple.Session
import models.support._

object GamesRequesteeController extends Controller with SecureSocialConsented {

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