package controllers.bank

import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import controllers.quiz.QuestionsController._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.{FriendRequest, UnfriendRequest}
import controllers.game.GamesEmail._
import controllers.organization.CoursesController._
import models.quiz.question._
import models.user.{Friend, Friends, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import com.artclod.random._
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service._
import play.api.data.format.Formats._

object QuestionBankController extends Controller with SecureSocialConsented {

  def list = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list())
  }


  def delete(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list())
  }


  def view(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list())
//    QuestionsController(questionId) match {
//      case Left(notFoundResult) => notFoundResult
//      case Right((organization, course , quiz, question)) => question match {
//        case derivative :       DerivativeQuestion       => Ok(views.html.bank.question.viewDerivativeQuestion(course, quiz, derivative.results(user), None))
//        case derivativeGraph :  DerivativeGraphQuestion  => Ok(views.html.quiz.derivativegraph.questionView(course, quiz, derivativeGraph.results(user), None))
//        case tangent :          TangentQuestion          => Ok(views.html.quiz.tangent.questionView(course, quiz, tangent.results(user), None))
//        case graphMatch :       GraphMatchQuestion       => Ok(views.html.quiz.graphmatch.questionView(course, quiz, graphMatch.results(user), None))
//        case polyZone :         PolynomialZoneQuestion   => Ok(views.html.quiz.polynomialzone.questionView(course, quiz, polyZone.results(user), None))
//        case multipleChoice :   MultipleChoiceQuestion   => Ok(views.html.quiz.multiplechoice.questionView(course, quiz, multipleChoice.results(user), None))
//        case multipleFunction : MultipleFunctionQuestion => Ok(views.html.quiz.multiplefunction.questionView(course, quiz, multipleFunction.results(user), None))
//      }
//    }
  }

}