package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction

object DerivativeQuestionSetLinksModel {
	val table = new DerivativeQuestionSetLinksTable

	def all()(implicit s: Session) = Query(table).list

	def create(questionId: Long, questionSetId: Long)(implicit s: Session) = table.insert(questionId, questionSetId)

	def create(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = table.insertAll(questionIds.map((_, questionSetId)): _*)
	
	def read(questionSetId: Long)(implicit s: Session) = Query(table).where(_.questionSetId === questionSetId).list.map(_._1)
	
	def update(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = {
		// LATER there may be a more efficient way then deleting all and then recreating
		table.where(_.questionSetId === questionSetId).delete
		table.insertAll(questionIds.map((_, questionSetId)): _*)
	}

	def delete(questionId: Long, questionSetId: Long)(implicit s: Session) = Query(table).where(r => r.questionId === questionId && r.questionSetId === questionSetId).delete

	def questions(questionSetId: Long)(implicit s: Session) = (for {
		l <- Query(table) if l.questionSetId === questionSetId
		q <- Query(DerivativeQuestionsModel.table) if l.questionId === q.id
	} yield q).list
}

class DerivativeQuestionSetLinksTable extends Table[(Long, Long)]("derivative_question_set_links") {
	def questionId = column[Long]("question_id", O.NotNull)
	def questionSetId = column[Long]("question_set_id", O.NotNull)
	def * = questionId ~ questionSetId

	def pk = primaryKey("derivative_question_set_links_pk", (questionId, questionSetId))

	def questionIdFK = foreignKey("derivative_question_set_links_question_fk", questionId, new DerivativeQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionSetIdFK = foreignKey("derivative_question_set_links_question_set_fk", questionSetId, new DerivativeQuestionSetsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}