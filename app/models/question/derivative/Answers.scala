package models.question.derivative

import org.joda.time.DateTime
import com.artclod.slick.Joda
import com.artclod.mathml._
import com.artclod.mathml.scalar.MathMLElem
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table._
import models.support._
import models.question.AsciiMathML
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
    val groupBy = answersTable.where(_.questionId === qid).groupBy(a => a.ownerId)//.map(r => (r._1, r._2.list)).list
    groupBy.map({case(ownerId, answers) => ownerId }).list

//    groupBy.map(_).list
//    groupBy.list
//      case (user, answers) => user -> answers.list)
//    )

//      .map(m => (m._1, m._2.list)).list
//    answersTable.where(v => v.questionId === qid )

//    null
  }

  // ======== TIME ======
  def startWorkingOn(questionId: QuestionId)(implicit user: User, session: Session) =
    if(startWorkTime(questionId).isEmpty) { answerTimesTable += AnswerTime(user.id, questionId, Joda.now) }

  def startWorkTime(questionId: QuestionId)(implicit user: User, session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).firstOption

  def startedWork(user: User, questionId: QuestionId)(implicit session: Session) =
    answerTimesTable.where(r => r.userId === user.id && r.questionId === questionId).sortBy(_.time).firstOption

}

