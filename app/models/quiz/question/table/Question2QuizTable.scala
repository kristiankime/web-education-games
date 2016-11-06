package models.quiz.question.table

import com.artclod.slick.JodaUTC._
import models.quiz.question.{Question2Quiz, Question}
import models.support.{QuizId, QuestionId, UserId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

trait Question2QuizTable extends Table[Question2Quiz] {
  def questionId = column[QuestionId]("id")
  def quizId = column[QuizId]("quiz")
  def ownerId = column[UserId]("owner")
  def creationDate = column[DateTime]("creation_date")
  def order = column[Int]("order")
}
