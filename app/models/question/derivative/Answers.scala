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
import models.id.Ids._

case class Answer(id: AnswerId, questionId: Long, mathML: MathMLElem, rawStr: String, synched: Boolean, correct: Boolean)

object Answers {

	def createAnswer(answerer: User, question: Question, rawStr: String, mathML: MathMLElem, synched: Boolean): AnswerId = DB.withSession { implicit session: Session =>
		val answerId = AnswersTable.autoInc.insert(question.id, mathML, rawStr, synched, correct(question, mathML))
		UsersAnswersTable.insert(answerer, answerId)
		answerId
	}

	private def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

	def findAnswers(qid: Long) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(_.questionId === qid).list
	}

	def findAnswer(qid: Long, aid: AnswerId) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption
	}

	def deleteAnswer(id: AnswerId) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(_.id === id).delete
	}
}

