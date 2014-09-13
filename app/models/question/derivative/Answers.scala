package models.question.derivative

import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question.AsciiMathML
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.User

case class Answer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML with Owned

object UnfinishedAnswer {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): Answer =
    Answer(null, ownerId, questionId, mathML, rawStr, correct, creationDate)
}

object Answers {

  def all(implicit session: Session) = answersTable.list()

  def correct(question: Question, mathML: MathMLElem) = MathMLEq.checkEq("x", question.mathML.d("x"), mathML)

  // ======= CREATE ======
  def createAnswer(answer: Answer)(implicit session: Session) = {
    val answerId = (answersTable returning answersTable.map(_.id)) += answer
    answer.copy(id = answerId)
  }

  // ======= FIND ======
  def apply(aid: AnswerId)(implicit session: Session) = answersTable.where(_.id === aid).firstOption

  def apply(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def apply(qid: QuestionId, aid: AnswerId)(implicit session: Session) = answersTable.where(v => v.questionId === qid && v.id === aid).firstOption

  def byUser(qid: QuestionId)(implicit session: Session) = {
    val groupBy = answersTable.where(_.questionId === qid).groupBy(a => a.ownerId)
    groupBy.map({ case (ownerId, answers) => ownerId}).list
  }

  // ======== TIME ======
  def startWorkingOn(questionId: QuestionId)(implicit user: User, session: Session) =
    if (startWorkTime(questionId).isEmpty) { answerTimesTable += AnswerTime(user.id, questionId, JodaUTC.now) }

  def startWorkTime(questionId: QuestionId)(implicit user: User, session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).firstOption

  def startedWork(user: User, questionId: QuestionId)(implicit session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).sortBy(_.time).firstOption

  // ======= ATTEMPTS SUMMARY ======
  def summary(user: User)(implicit session: Session) = {
    val q = answersTable.where(_.ownerId === user.id).groupBy(a => a.questionId)
    val q2 = q.map { case (questionId, v) => (questionId, v.length, v.map(_.correct).max, v.map(_.creationDate).min) }
    val q3 = q2.sortBy(_._4)
    q2.list.map(e => AnswersSummary(e._1, e._2, toBoolean(e._3), toDatetime(e._4)))
  }

  case class AnswersSummary(questionId: QuestionId, attempts : Int, correct: Boolean, firstAttempt: DateTime)

  private def toBoolean(v : Option[Boolean]) = v match {
    case None => false
    case Some(v) => v
  }

  private def toDatetime(v : Option[DateTime]) = v match {
    case None => JodaUTC.zero
    case Some(v) => v
  }

}

