package models.quiz.answer

import com.artclod.mathml.Match._
import com.artclod.mathml.{No, Yes}
import models.quiz.question.{GraphMatchQuestion}
import models.quiz.table.{AnswerIdNext, graphMatchAnswersTable}
import play.api.db.slick.Config.driver.simple._

object GraphMatchAnswers {

  def correct(question: GraphMatchQuestion, guessIndex: Short) = if(question.graphThis == guessIndex) { Yes } else { No }

  // ======= CREATE ======
  def createAnswer(answer: GraphMatchAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    graphMatchAnswersTable += toInsert
    toInsert
  }

}
