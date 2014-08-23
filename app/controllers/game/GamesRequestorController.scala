package controllers.game

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.game.GamesController._
import controllers.question.derivative.QuestionsController._
import controllers.question.derivative.{routes, QuizzesController}
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import models.question.derivative.{Question, Questions}
import models.support._
import play.api.data.Form
import play.api.data.Form._
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.db.slick.Config.driver.simple.Session
import service.User

object GamesRequestorController extends Controller with SecureSocialConsented {

  def requestorGameViews(organization: Organization, course: Course, game: Game)(implicit user: User, session: Session) = {
    game.toState match {
      case gameState: GameState with RequestorQuizUnfinished => Ok(views.html.game.createQuizRequestor(organization, course, gameState))
      case _ =>  throw new IllegalStateException("Not tor state mach, TODO this should be removeable via sealed")
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
