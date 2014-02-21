package models.question.derivative

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import service._
import models.question.derivative.table._
import models.id._
import org.joda.time.DateTime
import models.question.AsciiMathML

case class Answer(id: AnswerId, owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML

case class AnswerTmp(owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correct: Boolean, creationDate: DateTime) extends AsciiMathML {
	def apply(id: AnswerId) = Answer(id, owner, questionId, mathML, rawStr, correct, creationDate)
}

object AnswerTmp{
	def apply(owner: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean) : AnswerTmp = AnswerTmp(owner, questionId, mathML, rawStr, correct, creationDate)
}


object Answers {

	def find(aid: AnswerId)(implicit session: Session) = Query(new AnswersTable).where(_.id === aid).firstOption

	def createAnswer(answerTmp: AnswerTmp)(implicit session: Session) = answerTmp((new AnswersTable).insert(answerTmp))
	
	def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathMLEq.checkEq("x", question.mathML.d("x"), mathML)

	def findAnswers(qid: QuestionId)(implicit session: Session) = Query(new AnswersTable).where(_.questionId === qid).list
	
	def findAnswer(qid: QuestionId, aid: AnswerId)(implicit session: Session) = Query(new AnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption

	def deleteAnswer(id: AnswerId)(implicit session: Session) = Query(new AnswersTable).where(_.id === id).delete

}

