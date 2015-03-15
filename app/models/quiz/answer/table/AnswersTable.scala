package models.quiz.answer.table

import com.artclod.slick.JodaUTC._
import com.artclod.mathml.scalar.MathMLElem
import models.quiz.answer.Answer
import models.support.{AnswerId, QuizId, UserId, QuestionId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

trait AnswersTable[A <: Answer] extends Table[A] {
  def id = column[AnswerId]("id", O.PrimaryKey)
  def ownerId = column[UserId]("owner")
  def questionId = column[QuestionId]("question_id")
  def correct = column[Short]("correct") // Note this represent a Boolean in the Answers Class, kept as a number for aggregation purposes
  def creationDate = column[DateTime]("creation_date")
}
