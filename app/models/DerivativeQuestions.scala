package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class DerivativeQuestion(id: Long, mathML: MathMLElem, rawStr: String, synched: Boolean)

object DerivativeQuestions {
	val DerivativeQuestions = new DerivativeQuestions

	def all()(implicit s: Session) = Query(DerivativeQuestions).list

	def create(mathML: MathMLElem, rawStr: String, synched: Boolean)(implicit s: Session): Long = DerivativeQuestions.autoInc.insert(mathML, rawStr, synched)

	def read(id: Long)(implicit s: Session) = Query(DerivativeQuestions).where(_.id === id).firstOption

	def delete(id: Long)(implicit s: Session) = Query(DerivativeQuestions).where(_.id === id).delete
}

class DerivativeQuestions extends Table[DerivativeQuestion]("derivative_questions") {
	implicit val mathMLTypeMapper = MappedTypeMapper.base[MathMLElem, String](
		{ mathML => mathML.toString },
		{ string => MathML(string).getOrElse(Math(`0`)) })

	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def * = id ~ mathML ~ rawStr ~ synched <> (DerivativeQuestion, DerivativeQuestion.unapply _)
	
	def autoInc = mathML ~ rawStr ~ synched returning id
}

