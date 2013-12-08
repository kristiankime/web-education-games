package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction

case class DerivativeQuestionSet(id: Long, name: String, questions: List[DerivativeQuestion]) {
	val questionIds = questions.map(_.id)
	
	def toRow = DerivativeQuestionSetRow(id, name)
}

case class DerivativeQuestionSetRow(id: Long, name: String)

object DerivativeQuestionSetsModel {
	val table = new DerivativeQuestionSetsTable

	def all()(implicit s: Session) = Query(table).list

	def create(name: String, questionIds: List[Long])(implicit s: Session) = {
		val questionSetId = table.autoInc.insert(name)
		DerivativeQuestionSetLinksModel.create(questionSetId, questionIds)
		DerivativeQuestionSet(questionSetId, name, DerivativeQuestionsModel.read(questionIds))
	}

	def read(id: Long)(implicit s: Session) = {
		Query(table).where(_.id === id).firstOption.map{r => 
			val questionIds = DerivativeQuestionSetLinksModel.read(r.id)
			DerivativeQuestionSet(r.id, r.name, DerivativeQuestionsModel.read(questionIds))
		}
	}

	def update(set: DerivativeQuestionSet)(implicit s: Session) = {
		table.where(_.id === set.id).update(set.toRow)
		DerivativeQuestionSetLinksModel.update(set.id, set.questionIds);
		set
	}

	def delete(id: Long)(implicit s: Session) = table.where(_.id === id).delete
}

class DerivativeQuestionSetsTable extends Table[DerivativeQuestionSetRow]("derivative_question_sets") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (DerivativeQuestionSetRow, DerivativeQuestionSetRow.unapply _)

	def autoInc = name returning id
}
