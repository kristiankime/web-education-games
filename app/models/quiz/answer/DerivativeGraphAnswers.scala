package models.quiz.answer

import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.{DerivativeGraphQuestion, DerivativeQuestion}
import models.quiz.table.{AnswerIdNext, derivativeGraphAnswersTable}
import play.api.db.slick.Config.driver.simple._
import com.artclod.mathml.Match._

object DerivativeGraphAnswers {

  def correct(question: DerivativeGraphQuestion, derivativeOrder: DerivativeOrder) = if(question.derivativeOrder == derivativeOrder) { Yes } else { No }

  // ======= CREATE ======
  def createAnswer(answer: DerivativeGraphAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    derivativeGraphAnswersTable += toInsert
    toInsert
  }

}
