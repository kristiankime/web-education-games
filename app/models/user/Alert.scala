package models.user

import models.game.Games
import models.support.{AlertId, QuizId, UserId, GameId}
import org.joda.time.DateTime
import play.api.templates.Html
import play.api.db.slick.Config.driver.simple.Session

sealed trait Alert {
  val id: AlertId
  val recipientId: UserId
  val seen: Boolean
  val creationDate: DateTime
  val displayScore: Boolean

  def recipientOp(implicit session: Session) = Users(recipientId)

  def display(implicit session: Session) : Html
}

case class GameCompletedAlert(id: AlertId, recipientId: UserId, seen: Boolean, creationDate: DateTime, gameId: GameId, teacherPoints: Int, studentPoints: Int, quizId: QuizId) extends Alert {
  val displayScore = true

  def game(implicit session: Session) = Games(gameId).get

  def display(implicit session: Session) : Html = views.html.alert.gameCompletedAlert(this)
}