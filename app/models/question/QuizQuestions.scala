package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table.DerivativeQuestionSetLinksTable
import models.question.table.DerivativeQuestionsTable

object QuizQuestions {
	def all()(implicit s: Session) = Query(DerivativeQuestionSetLinksTable).list

	def create(questionId: Long, questionSetId: Long)(implicit s: Session) = DerivativeQuestionSetLinksTable.insert(questionId, questionSetId)

	def create(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = DerivativeQuestionSetLinksTable.insertAll(questionIds.map((_, questionSetId)): _*)
	
	def read(questionSetId: Long)(implicit s: Session) = Query(DerivativeQuestionSetLinksTable).where(_.questionSetId === questionSetId).list.map(_._1)
	
	def update(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = {
		// LATER there may be a more efficient way then deleting all and then recreating
		DerivativeQuestionSetLinksTable.where(_.questionSetId === questionSetId).delete
		DerivativeQuestionSetLinksTable.insertAll(questionIds.map((_, questionSetId)): _*)
	}

	def delete(questionId: Long, questionSetId: Long)(implicit s: Session) = Query(DerivativeQuestionSetLinksTable).where(r => r.questionId === questionId && r.questionSetId === questionSetId).delete

	def questions(questionSetId: Long)(implicit s: Session) = (for {
		l <- Query(DerivativeQuestionSetLinksTable) if l.questionSetId === questionSetId
		q <- Query(DerivativeQuestionsTable) if l.questionId === q.id
	} yield q).list
}
