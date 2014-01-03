package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.mapper.MathMLMapper._
import models.security.User
import models.question.authorization.UserQuestionsTable

case class DerivativeQuestion(id: Long, mathML: MathMLElem, rawStr: String, synched: Boolean)

object DerivativeQuestionsModel {
	val table = new DerivativeQuestionsTable

	def all()(implicit s: Session) = Query(table).list

	def create(owner: User, mathML: MathMLElem, rawStr: String, synched: Boolean)(implicit s: Session): Long = {
		val qid = table.autoInc.insert(mathML, rawStr, synched)
		UserQuestionsTable.create(owner, qid)
		qid
	}

	def read(id: Long)(implicit s: Session) = Query(table).where(_.id === id).firstOption

	def read(ids: List[Long])(implicit s: Session) = Query(table).where(_.id inSet ids).list
	
	def delete(id: Long)(implicit s: Session) = table.where(_.id === id).delete
}

class DerivativeQuestionsTable extends Table[DerivativeQuestion]("derivative_questions") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def * = id ~ mathML ~ rawStr ~ synched <> (DerivativeQuestion, DerivativeQuestion.unapply _)	
	def autoInc = mathML ~ rawStr ~ synched returning id
}

