package models.question.derivative

import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question.{Correct2Short, ViewableMath}
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.User
import models.question.table.answersTable

object DerivativeAnswers {

  def all(implicit session: Session) = answersTable.list()

  def correct(question: DerivativeQuestion, mathML: MathMLElem) = MathMLEq.checkEq("x", question.mathML.d("x"), mathML)

  // ======= CREATE ======
  def createAnswer(answer: DerivativeAnswer)(implicit session: Session) = {
    val answerId = (answersTable returning answersTable.map(_.id)) += answer
    answer.copy(id = answerId)
  }

  // ======= FIND ======
  def apply(aid: AnswerId)(implicit session: Session) : Option[DerivativeAnswer] = answersTable.where(_.id === aid).firstOption

  def apply(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def apply(qid: QuestionId, aid: AnswerId)(implicit session: Session) = answersTable.where(v => v.questionId === qid && v.id === aid).firstOption

  def byUser(qid: QuestionId)(implicit session: Session) = {
    val groupBy = answersTable.where(_.questionId === qid).groupBy(a => a.ownerId)
    groupBy.map({ case (ownerId, answers) => ownerId}).list
  }

  // ======= ATTEMPTS SUMMARY ======
  case class AnswersSummary(questionId: QuestionId, attempts : Int, correct: Boolean, firstAttempt: DateTime)

  def summary(user: User)(implicit session: Session) = {
    val q = answersTable.where(_.ownerId === user.id).groupBy(a => a.questionId)
    val q2 = q.map { case (questionId, v) => (questionId, v.length, v.map(_.correct).max, v.map(_.creationDate).min) }
    val q3 = q2.sortBy(_._4)
    q2.list.map(e => AnswersSummary(e._1, e._2, Correct2Short(e._3.get), e._4.get))
  }

}
