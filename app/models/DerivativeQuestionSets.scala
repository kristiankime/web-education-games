package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ForeignKeyAction

case class DerivativeQuestionSet(id: Long, name: String) {
	def questions(implicit s: Session) = DerivativeQuestionSetLinks.questions(id)
}

object DerivativeQuestionSets {
	val DerivativeQuestionSets = new DerivativeQuestionSets

	def all()(implicit s: Session) = Query(DerivativeQuestionSets).list

	def create(name: String)(implicit s: Session): Long = DerivativeQuestionSets.autoInc.insert(name)
	
	def read(id: Long)(implicit s: Session) = Query(DerivativeQuestionSets).where(_.id === id).firstOption

	def delete(id: Long)(implicit s: Session) = Query(DerivativeQuestionSets).where(_.id === id).delete
}

class DerivativeQuestionSets extends Table[DerivativeQuestionSet]("derivative_question_sets") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (DerivativeQuestionSet, DerivativeQuestionSet.unapply _)

	def autoInc = name returning id
}


