package models.quiz.question.table

import com.artclod.mathml.scalar.MathMLElem
import models.quiz.question.Question
import models.support.{QuizId, UserId, QuestionId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.artclod.slick.JodaUTC._

trait QuestionsTable[Q <: Question] extends Table[Q] {
  def id = column[QuestionId]("id", O.PrimaryKey)
  def ownerId = column[UserId]("owner")
  def creationDate = column[DateTime]("creation_date")
  def atCreationDifficulty = column[Double]("at_creation_difficulty")
//  def quizId = column[Option[QuizId]]("quiz_id")
  def order = column[Int]("order")
}
