package models.organization.table

import models.organization.{GameResponseStatus, Game}
import models.question.derivative.table._
import models.support._
import com.artclod.slick.JodaUTC._
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction

class GamesTable(tag: Tag) extends Table[Game](tag, "games") {
  def id = column[GameId]("id", O.PrimaryKey, O.AutoInc)
  def requestDate = column[DateTime]("request_date")
  def requestor = column[UserId]("requestor")
  def requestee = column[UserId]("requestee")
  def response = column[GameResponseStatus]("response")
  def course = column[Option[CourseId]]("course")
  def requestorQuiz = column[Option[QuizId]]("requestor_quiz")
  def requestorQuizDone = column[Boolean]("requestor_quiz_done")
  def requesteeQuiz = column[Option[QuizId]]("requestee_quiz")
  def requesteeQuizDone = column[Boolean]("requestee_quiz_done")
  def requesteeFinished = column[Boolean]("requestee_finished")
  def requestorFinished = column[Boolean]("requestor_finished")
  def finishedDate = column[Option[DateTime]]("finished_date")

  def * = (id, requestDate, requestor, requestee, response, course, requestorQuiz, requestorQuizDone, requesteeQuiz, requesteeQuizDone, requesteeFinished, requestorFinished, finishedDate) <> (Game.tupled, Game.unapply _)

  def requestorFK = foreignKey("games_requestor_fk", requestor, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeFK = foreignKey("games_requestee_fk", requestee, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("games_course_fk", course, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requestorQuizFK = foreignKey("games_requestor_quiz_fk", requestorQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeQuizFK = foreignKey("games_requestee_quiz_fk", requesteeQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
