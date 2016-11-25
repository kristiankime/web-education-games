package models.quiz.answer

import com.artclod.mathml.Match._
import com.artclod.mathml.{No, Yes}
import models.quiz.question.{MultipleChoiceQuestion}
import models.quiz.table.{AnswerIdNext, multipleChoiceAnswersTable}
import play.api.db.slick.Config.driver.simple._

object MultipleChoiceAnswers {

  def correct(question: MultipleChoiceQuestion, guessIndex: Short) = (question.correctAnswer == guessIndex)

  // ======= CREATE ======
  def createAnswer(answer: MultipleChoiceAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    multipleChoiceAnswersTable += toInsert
    toInsert
  }

}
