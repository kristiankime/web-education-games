package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.mapper.MathMLMapper._
import models.question.table._
import play.api.db.slick.DB
import play.api.Play.current
import service.table.User

case class Answer(id: Long, questionId: Long, mathML: MathMLElem, rawStr: String, synched: Boolean, correct: Boolean)

object Answers {

	def createAnswer(answerer: User, question: Question, rawStr: String, mathML: MathMLElem, synched: Boolean): Long = DB.withSession { implicit session: Session =>
		val answerId = AnswersTable.autoInc.insert(question.id, mathML, rawStr, synched, correct(question, mathML))
		UsersAnswersTable.insert(answerer, answerId)
		answerId
	}

	private def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

	def findAnswers(qid: Long) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(_.questionId === qid).list
	}

	def findAnswer(qid: Long, aid: Long) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption
	}

	def deleteAnswer(id: Long) = DB.withSession { implicit session: Session =>
		Query(AnswersTable).where(_.id === id).delete
	}
}

