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

object Answers {

	def find(aid: AnswerId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.id === aid).firstOption
	}

	def createAnswer(answerTmp: AnswerTmp) = DB.withSession { implicit session: Session =>
		answerTmp((new AnswersTable).insert(answerTmp))
	}
	
	def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

	def findAnswers(qid: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.questionId === qid).list
	}

	def findAnswer(qid: QuestionId, aid: AnswerId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption
	}

	def deleteAnswer(id: AnswerId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.id === id).delete
	}
}

