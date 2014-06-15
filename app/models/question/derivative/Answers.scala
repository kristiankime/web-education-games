package models.question.derivative

import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.Joda
import models.question.AsciiMathML
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.User

case class Answer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML with Owned

object AnswerLater {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): Answer =
    Answer(null, ownerId, questionId, mathML, rawStr, correct, creationDate)
}

object Answers {

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
    if (startWorkTime(questionId).isEmpty) { answerTimesTable += AnswerTime(user.id, questionId, Joda.now) }

  def startWorkTime(questionId: QuestionId)(implicit user: User, session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).firstOption

  def startedWork(user: User, questionId: QuestionId)(implicit session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).sortBy(_.time).firstOption

}

