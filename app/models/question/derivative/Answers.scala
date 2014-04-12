package models.question.derivative

import com.artclod.mathml._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.MathMLElem
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import service._
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import models.question.AsciiMathML

case class AnswerTmp(owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML {
	def apply(id: AnswerId) = Answer(id, owner, questionId, mathML, rawStr, correct, creationDate)
}

object AnswerTmp {
	def apply(owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): AnswerTmp = AnswerTmp(owner, questionId, mathML, rawStr, correct, creationDate)
}

case class Answer(id: AnswerId, owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML

object Answers {

	def correct(question: Question, mathML: MathMLElem)= MathMLEq.checkEq("x", question.mathML.d("x"), mathML)

	// ======= CREATE ======
	def createAnswer(answerTmp: AnswerTmp)(implicit session: Session) = answerTmp((new AnswersTable).insert(answerTmp))

	// ======= FIND ======
	def find(aid: AnswerId)(implicit session: Session) = Query(new AnswersTable).where(_.id === aid).firstOption

	def findAnswers(qid: QuestionId)(implicit session: Session) = Query(new AnswersTable).where(_.questionId === qid).list

	def findAnswer(qid: QuestionId, aid: AnswerId)(implicit session: Session) = Query(new AnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption

}

