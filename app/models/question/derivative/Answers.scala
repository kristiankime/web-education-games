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

case class Answer(id: AnswerId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, synched: Boolean, correct: Boolean)

object Answers {

	def find(aid: AnswerId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.id === aid).firstOption
	}
	
	def createAnswer(answerer: User, question: Question, rawStr: String, mathML: MathMLElem, synched: Boolean): AnswerId = DB.withSession { implicit session: Session =>
		val answerId = (new AnswersTable).autoInc.insert(question.id, mathML, rawStr, synched, correct(question, mathML))
		(new UsersAnswersTable).insert(answerer, answerId)
		answerId
	}

	private def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

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

