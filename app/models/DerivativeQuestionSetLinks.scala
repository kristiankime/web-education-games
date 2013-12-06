package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction

object DerivativeQuestionSetLinks {
	val DerivativeQuestionSetLinks = new DerivativeQuestionSetLinks

	def all()(implicit s: Session) = Query(DerivativeQuestionSetLinks).list

	def create(questionId: Long, questionSetId: Long)(implicit s: Session) = DerivativeQuestionSetLinks.insert(questionId, questionSetId)

	def delete(questionId: Long, questionSetId: Long)(implicit s: Session) = Query(DerivativeQuestionSetLinks).where(r => r.questionId === questionId && r.questionSetId === questionSetId).delete

	def questions(questionSetId: Long)(implicit s: Session) = (for {
		l <- Query(DerivativeQuestionSetLinks) if l.questionSetId === questionSetId
		q <- Query(DerivativeQuestions.DerivativeQuestions) if l.questionId === q.id
	} yield q).list
}

class DerivativeQuestionSetLinks extends Table[(Long, Long)]("derivative_question_set_links") {
	def questionId = column[Long]("question_id", O.NotNull)
	def questionSetId = column[Long]("question_set_id", O.NotNull)
	def * = questionId ~ questionSetId

	def pk = primaryKey("derivative_question_set_links_pk", (questionId, questionSetId))

	def questionIdFK = foreignKey("derivative_question_set_links_question_fk", questionId, new DerivativeQuestions)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionSetIdFK = foreignKey("derivative_question_set_links_question_set_fk", questionSetId, new DerivativeQuestionSets)(_.id, onDelete = ForeignKeyAction.Cascade)
}