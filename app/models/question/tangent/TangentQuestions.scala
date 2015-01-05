package models.question.tangent

import com.artclod.mathml.slick.MathMLMapper
import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question._
import models.question.derivative.DerivativeQuestion
import models.question.derivative.result.DerivativeQuestionScores
import models.question.derivative.table._
import models.question.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.{Query, _}
import service._
import service.table.UsersTable

object TangentQuestions {

  def create(info: TangentQuestion, quizId: QuizId)(implicit session: Session): TangentQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    tangentQuestionsTable += toInsert
    toInsert
  }

}

