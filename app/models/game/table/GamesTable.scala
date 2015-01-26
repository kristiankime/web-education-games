package models.game.table

import com.artclod.slick.JodaUTC._
import models.game.{Game, GameResponseStatus}
import models.organization.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import scala.slick.model.ForeignKeyAction
import models.quiz.table.quizzesTable

class GamesTable(tag: Tag) extends Table[Game](tag, "games") {
  def id = column[GameId]("id", O.PrimaryKey, O.AutoInc)
  def requestDate = column[DateTime]("request_date")
  def requestor = column[UserId]("requestor")
  def requestorSkill = column[Double]("requestor_skill")
  def requestee = column[UserId]("requestee")
  def requesteeSkill = column[Double]("requestee_skill")
  def response = column[GameResponseStatus]("response")
  def course = column[Option[CourseId]]("course")
  def requestorQuiz = column[Option[QuizId]]("requestor_quiz")
  def requestorQuizDone = column[Boolean]("requestor_quiz_done")
  def requesteeQuiz = column[Option[QuizId]]("requestee_quiz")
  def requesteeQuizDone = column[Boolean]("requestee_quiz_done")
  def requesteeFinished = column[Boolean]("requestee_finished")
  def requestorFinished = column[Boolean]("requestor_finished")
  def requesteeStudentPoints = column[Option[Double]]("requestee_student_points")
  def requesteeTeacherPoints = column[Option[Double]]("requestee_teacher_points")
  def requestorStudentPoints = column[Option[Double]]("requestor_student_points")
  def requestorTeacherPoints = column[Option[Double]]("requestor_teacher_points")
  def finishedDate = column[Option[DateTime]]("finished_date")

  def * = (id, requestDate, requestor, requestorSkill, requestee, requesteeSkill, response, course,
    requestorQuiz, requestorQuizDone, requesteeQuiz, requesteeQuizDone,
    requesteeFinished, requestorFinished, requesteeStudentPoints, requesteeTeacherPoints, requestorStudentPoints, requestorTeacherPoints, finishedDate) <> (Game.tupled, Game.unapply _)

  def requestorFK = foreignKey("games__requestor_fk", requestor, LoginsTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeFK = foreignKey("games__requestee_fk", requestee, LoginsTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("games__course_fk", course, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requestorQuizFK = foreignKey("games__requestor_quiz_fk", requestorQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def requesteeQuizFK = foreignKey("games__requestee_quiz_fk", requesteeQuiz, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
