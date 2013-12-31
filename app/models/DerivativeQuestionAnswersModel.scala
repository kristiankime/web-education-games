package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction
import models.mapper.MathMLMapper._

case class DerivativeQuestionAnswer(id: Long, questionId: Long, mathML: MathMLElem, rawStr: String, synched: Boolean, correct: Boolean)

object DerivativeQuestionAnswersModel {
	val table = new DerivativeQuestionAnswersTable

	def all()(implicit s: Session) = Query(table).list

	def create(question: DerivativeQuestion, rawStr: String, mathML: MathMLElem, synched: Boolean)(implicit s: Session): Long =
		table.autoInc.insert(question.id, mathML, rawStr, synched, correct(question, mathML))

	private def correct(question: DerivativeQuestion, mathML: mathml.scalar.MathMLElem) = MathML.checkEq("x", question.mathML.d("x"), mathML)

	def read(qid: Long)(implicit s: Session) = Query(table).where(_.questionId === qid).list

	def read(qid: Long, aid: Long)(implicit s: Session) = Query(table).where(v => v.questionId === qid && v.id === aid).firstOption

	def delete(id: Long)(implicit s: Session) = Query(table).where(_.id === id).delete
}

class DerivativeQuestionAnswersTable extends Table[DerivativeQuestionAnswer]("derivative_question_answers") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[Long]("question_id", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def correct = column[Boolean]("correct", O.NotNull)
	def * = id ~ questionId ~ mathML ~ rawStr ~ synched ~ correct <> (DerivativeQuestionAnswer, DerivativeQuestionAnswer.unapply _)

	def autoInc = questionId ~ mathML ~ rawStr ~ synched ~ correct returning id

	def questionFK = foreignKey("question_fk", questionId, new DerivativeQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
