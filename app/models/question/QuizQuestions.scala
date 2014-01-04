package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table._

object QuizQuestions {
	def all()(implicit s: Session) = Query(QuizQuestionsTable).list

	def create(questionId: Long, questionSetId: Long)(implicit s: Session) = QuizQuestionsTable.insert(questionId, questionSetId)

	def create(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = QuizQuestionsTable.insertAll(questionIds.map((_, questionSetId)): _*)
	
	def read(questionSetId: Long)(implicit s: Session) = Query(QuizQuestionsTable).where(_.questionSetId === questionSetId).list.map(_._1)
	
	def update(questionSetId: Long, questionIds: List[Long])(implicit s: Session) = {
		// LATER there may be a more efficient way then deleting all and then recreating
		QuizQuestionsTable.where(_.questionSetId === questionSetId).delete
		QuizQuestionsTable.insertAll(questionIds.map((_, questionSetId)): _*)
	}

	def delete(questionId: Long, questionSetId: Long)(implicit s: Session) = Query(QuizQuestionsTable).where(r => r.questionId === questionId && r.questionSetId === questionSetId).delete

	def questions(questionSetId: Long)(implicit s: Session) = (for {
		l <- Query(QuizQuestionsTable) if l.questionSetId === questionSetId
		q <- Query(QuestionsTable) if l.questionId === q.id
	} yield q).list
}
