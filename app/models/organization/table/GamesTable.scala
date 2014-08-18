package models.organization.table

import models.organization.Game
import models.question.derivative.table._
import models.support._
import com.artclod.slick.JodaUTC._
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction

class GamesTable(tag: Tag) extends Table[Game](tag, "games") {
  def id = column[GameId]("id", O.PrimaryKey, O.AutoInc)
  def requestDate = column[DateTime]("request_date", O.NotNull)
  def requestor = column[UserId]("requestor", O.NotNull)
  def requestee = column[UserId]("requestee", O.NotNull)
  def requestAccepted = column[Boolean]("request_accepted", O.NotNull)
  def course = column[Option[CourseId]]("course", O.Nullable)
  def requestorQuiz = column[Option[QuizId]]("requestor_quiz", O.Nullable)
  def requesteeQuiz = column[Option[QuizId]]("requestee_quiz", O.Nullable)
  def requesteeFinished = column[Boolean]("requestee_finished", O.NotNull)
  def requestorFinished = column[Boolean]("requestor_finished", O.NotNull)
  def finishedDate = column[Option[DateTime]]("finished_date", O.Nullable)

  def * = (id, requestDate, requestor, requestee, requestAccepted, course, requestorQuiz, requesteeQuiz, requesteeFinished, requestorFinished, finishedDate) <> (Game.tupled, Game.unapply _)

  def requestorFK = foreignKey("games_requestor_fk", requestor, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeFK = foreignKey("games_requestee_fk", requestee, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("games_course_fk", course, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requestorQuizFK = foreignKey("games_requestor_quiz_fk", requestorQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeQuizFK = foreignKey("games_requestee_quiz_fk", requesteeQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
