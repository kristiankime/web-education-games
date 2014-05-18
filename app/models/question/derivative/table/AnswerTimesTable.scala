package models.question.derivative.table

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.support._
import service.table.UserTable

case class AnswerTime(userId: UserId, questionId: QuestionId, time: DateTime)

/**
 * Indicates when a user first started working on a question.
 */
class AnswerTimesTable extends Table[AnswerTime]("derivative_answer_times") {
  def userId = column[UserId]("user_id", O.NotNull)
  def questionId = column[QuestionId]("question_id", O.NotNull)
  def time = column[DateTime]("time", O.NotNull)

  def * = userId ~ questionId ~ time <> (AnswerTime, AnswerTime.unapply _)

  def pk = primaryKey("derivative_answer_times_pk", (userId, questionId))

  def quizIdFK = foreignKey("derivative_answer_times_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def questionIdFK = foreignKey("derivative_answer_times_question_fk", questionId, new QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
