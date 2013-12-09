package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction

case class DerivativeQuestionSet(id: Long, name: String)

object DerivativeQuestionSetsModel {
	val table = new DerivativeQuestionSetsTable

	def all()(implicit s: Session) = Query(table).list

	def create(name: String, questionIds: List[Long])(implicit s: Session) = {
		val id = table.autoInc.insert(name)
		DerivativeQuestionSetLinksModel.create(id, questionIds)
		id
	}

	def read(id: Long)(implicit s: Session) = {
		Query(table).where(_.id === id).firstOption.map{r => 
			(DerivativeQuestionSet(r.id, r.name), DerivativeQuestionSetLinksModel.read(r.id))
		}
	}
	
	def readQuestion(id: Long)(implicit s: Session) = {
		Query(table).where(_.id === id).firstOption.map{r => 
			val qids =  DerivativeQuestionSetLinksModel.read(r.id)
			val qs = DerivativeQuestionsModel.read(qids)
			(DerivativeQuestionSet(r.id, r.name), qs)
		}
	}

	def update(set: DerivativeQuestionSet, questionIds: List[Long])(implicit s: Session) = {
		table.where(_.id === set.id).update(set)
		DerivativeQuestionSetLinksModel.update(set.id, questionIds);
	}

	def delete(id: Long)(implicit s: Session) = table.where(_.id === id).delete
}

class DerivativeQuestionSetsTable extends Table[DerivativeQuestionSet]("derivative_question_sets") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (DerivativeQuestionSet, DerivativeQuestionSet.unapply _)

	def autoInc = name returning id
}
