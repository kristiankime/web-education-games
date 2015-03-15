package models.quiz.answer

import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.NumericBoolean
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.quiz.question.DerivativeQuestion
import models.quiz.table.{AnswerIdNext, derivativeAnswersTable}
import models.support._
import models.user.User
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

object DerivativeAnswers {

  def correct(question: DerivativeQuestion, mathML: MathMLElem) = MathMLEq.checkEq("x", question.mathML.d("x"), mathML)

  // ======= CREATE ======
  def createAnswer(answer: DerivativeAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    derivativeAnswersTable += toInsert
    toInsert
  }

}
