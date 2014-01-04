package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.question.table._

case class Quiz(id: Long, name: String)

object Quizes {
	def all()(implicit s: Session) = Query(QuizesTable).list

	def create(name: String, questionIds: List[Long])(implicit s: Session) = {
		val id = QuizesTable.autoInc.insert(name)
		QuizQuestions.create(id, questionIds)
		id
	}

	def read(id: Long)(implicit s: Session) =
		Query(QuizesTable).where(_.id === id).firstOption.map { r => Quiz(r.id, r.name) }

	def readIds(id: Long)(implicit s: Session) =
		Query(QuizesTable).where(_.id === id).firstOption.map {
			r => (Quiz(r.id, r.name), QuizQuestions.read(r.id))
		}

	def readQuestion(id: Long)(implicit s: Session) =
		Query(QuizesTable).where(_.id === id).firstOption.map { r =>
			val qids = QuizQuestions.read(r.id)
			val qs = Questions.read(qids)
			(Quiz(r.id, r.name), qs)
		}

	def update(set: Quiz, questionIds: List[Long])(implicit s: Session) = {
		QuizesTable.where(_.id === set.id).update(set)
		QuizQuestions.update(set.id, questionIds);
	}

	def delete(id: Long)(implicit s: Session) = QuizesTable.where(_.id === id).delete
}
