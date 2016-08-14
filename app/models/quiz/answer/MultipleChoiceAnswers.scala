package models.quiz.answer

import com.artclod.mathml.Match._
import models.quiz.question.{MultipleChoiceQuestion}
import models.quiz.table.{AnswerIdNext, multipleChoiceAnswersTable}
import play.api.db.slick.Config.driver.simple._

object MultipleChoiceAnswers {

  def correct(question: MultipleChoiceQuestion, guessIndex: Short) = if(question.correctAnswer == guessIndex) { Yes } else { No }

  // ======= CREATE ======
  def createAnswer(answer: MultipleChoiceAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    multipleChoiceAnswersTable += toInsert
    toInsert
  }

}
