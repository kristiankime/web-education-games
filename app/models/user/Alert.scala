package models.user

import models.game.Games
import models.support.{QuizId, UserId, GameId}
import org.joda.time.DateTime
import play.api.templates.Html
import play.api.db.slick.Config.driver.simple.Session

sealed trait Alert {
  val recipientId: UserId
  val seen: Boolean
  val creationDate: DateTime

  def recipientOp(implicit session: Session) = Users(recipientId)

  def display : Html
}

case class GameCompletedAlert(recipientId: UserId, seen: Boolean, creationDate: DateTime, gameId: GameId, teacherPoints: Int, studentPoints: Int, quizId: QuizId) extends Alert {

  def game(implicit session: Session) = Games(gameId).get

  def display : Html = views.html.alert.gameCompletedAlert(this)

}