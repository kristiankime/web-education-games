package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.question.table.DerivativeQuestionSetsTable

case class DerivativeQuestionSet(id: Long, name: String)

object DerivativeQuestionSetsModel {
	def all()(implicit s: Session) = Query(DerivativeQuestionSetsTable).list

	def create(name: String, questionIds: List[Long])(implicit s: Session) = {
		val id = DerivativeQuestionSetsTable.autoInc.insert(name)
		DerivativeQuestionSetLinksModel.create(id, questionIds)
		id
	}

	def read(id: Long)(implicit s: Session) =
		Query(DerivativeQuestionSetsTable).where(_.id === id).firstOption.map { r => DerivativeQuestionSet(r.id, r.name) }

	def readIds(id: Long)(implicit s: Session) =
		Query(DerivativeQuestionSetsTable).where(_.id === id).firstOption.map {
			r => (DerivativeQuestionSet(r.id, r.name), DerivativeQuestionSetLinksModel.read(r.id))
		}

	def readQuestion(id: Long)(implicit s: Session) =
		Query(DerivativeQuestionSetsTable).where(_.id === id).firstOption.map { r =>
			val qids = DerivativeQuestionSetLinksModel.read(r.id)
			val qs = DerivativeQuestionsModel.read(qids)
			(DerivativeQuestionSet(r.id, r.name), qs)
		}

	def update(set: DerivativeQuestionSet, questionIds: List[Long])(implicit s: Session) = {
		DerivativeQuestionSetsTable.where(_.id === set.id).update(set)
		DerivativeQuestionSetLinksModel.update(set.id, questionIds);
	}

	def delete(id: Long)(implicit s: Session) = DerivativeQuestionSetsTable.where(_.id === id).delete
}
