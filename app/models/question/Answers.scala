package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.mapper.MathMLMapper._
import models.question.table.DerivativeQuestionAnswersTable

case class Answer(id: Long, questionId: Long, mathML: MathMLElem, rawStr: String, synched: Boolean, correct: Boolean)

object Answers {
	def all()(implicit s: Session) = Query(DerivativeQuestionAnswersTable).list

	def create(question: Question, rawStr: String, mathML: MathMLElem, synched: Boolean)(implicit s: Session): Long =
		DerivativeQuestionAnswersTable.autoInc.insert(question.id, mathML, rawStr, synched, correct(question, mathML))

	private def correct(question: Question, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

	def read(qid: Long)(implicit s: Session) = Query(DerivativeQuestionAnswersTable).where(_.questionId === qid).list

	def read(qid: Long, aid: Long)(implicit s: Session) = Query(DerivativeQuestionAnswersTable).where(v => v.questionId === qid && v.id === aid).firstOption

	def delete(id: Long)(implicit s: Session) = Query(DerivativeQuestionAnswersTable).where(_.id === id).delete
}

