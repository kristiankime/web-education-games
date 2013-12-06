package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction

object DerivativeQuestionSetLinksModel {
	val DerivativeQuestionSetLinks = new DerivativeQuestionSetLinksModel

	def all()(implicit s: Session) = Query(DerivativeQuestionSetLinks).list

	def create(questionId: Long, questionSetId: Long)(implicit s: Session) = DerivativeQuestionSetLinks.insert(questionId, questionSetId)

	def create(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = DerivativeQuestionSetLinks.insertAll(questionIds.map((_, questionSetId)): _*)
	
	def update(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = {
		// LATER there may be a more efficient way then deleting all and then recreating
		DerivativeQuestionSetLinks.where(_.questionSetId === questionSetId).delete
		DerivativeQuestionSetLinks.insertAll(questionIds.map((_, questionSetId)): _*)
	}

	def delete(questionId: Long, questionSetId: Long)(implicit s: Session) = Query(DerivativeQuestionSetLinks).where(r => r.questionId === questionId && r.questionSetId === questionSetId).delete

	def questions(questionSetId: Long)(implicit s: Session) = (for {
		l <- Query(DerivativeQuestionSetLinks) if l.questionSetId === questionSetId
		q <- Query(DerivativeQuestionsModel.DerivativeQuestions) if l.questionId === q.id
	} yield q).list
}

class DerivativeQuestionSetLinksModel extends Table[(Long, Long)]("derivative_question_set_links") {
	def questionId = column[Long]("question_id", O.NotNull)
	def questionSetId = column[Long]("question_set_id", O.NotNull)
	def * = questionId ~ questionSetId

	def pk = primaryKey("derivative_question_set_links_pk", (questionId, questionSetId))

	def questionIdFK = foreignKey("derivative_question_set_links_question_fk", questionId, new DerivativeQuestionsModel)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionSetIdFK = foreignKey("derivative_question_set_links_question_set_fk", questionSetId, new DerivativeQuestionSetsModel)(_.id, onDelete = ForeignKeyAction.Cascade)
}