package models.question

import com.artclod.mathml.scalar.MathMLElem
import models.support.{Owned, QuizId, UserId, QuestionId}
import org.joda.time.DateTime

trait Question extends Owned {
  val id: QuestionId
  val ownerId: UserId
  val creationDate: DateTime
  val quizIdOp: Option[QuizId]
  val order: Int
}
