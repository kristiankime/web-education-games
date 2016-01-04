package models.user.table

import models.support._
import models.user.{GameCompletedAlert}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class GameCompletedAlertsTable(tag: Tag) extends Table[GameCompletedAlert](tag, "alert_game_completed") with AlertsTable[GameCompletedAlert] {
  def gameId =  column[GameId]("game_id")
  def teacherPoints =  column[Int]("teacher_points")
  def studentPoints =  column[Int]("students_points")
  def quizId =  column[QuizId]("quiz_id")

  def * = (id, recipientId, seen, creationDate, gameId, teacherPoints, studentPoints, quizId) <> (GameCompletedAlert.tupled, GameCompletedAlert.unapply _)

  def userFK = foreignKey("alert_game_completed__user_fk", recipientId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def gameFK = foreignKey("alert_game_completed__game_fk", gameId, models.game.table.gamesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def quizFK = foreignKey("alert_game_completed__quiz_fk", quizId, models.quiz.table.quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def recipientIdSeenIndex = index("alert_game_completed__name_index", (recipientId, seen))
}

