package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.mapper.MathMLMapper._
import models.security.User
import models.question.authorization.UserQuestionsTable
import models.question.table.DerivativeQuestionsTable

case class DerivativeQuestion(id: Long, mathML: MathMLElem, rawStr: String, synched: Boolean)

object DerivativeQuestionsModel {
	def all()(implicit s: Session) = Query(DerivativeQuestionsTable).list

	def create(owner: User, mathML: MathMLElem, rawStr: String, synched: Boolean)(implicit s: Session): Long = {
		val qid = DerivativeQuestionsTable.autoInc.insert(mathML, rawStr, synched)
		UserQuestionsTable.create(owner, qid)
		qid
	}

	def read(id: Long)(implicit s: Session) = Query(DerivativeQuestionsTable).where(_.id === id).firstOption

	def read(ids: List[Long])(implicit s: Session) = Query(DerivativeQuestionsTable).where(_.id inSet ids).list

	def delete(id: Long)(implicit s: Session) = DerivativeQuestionsTable.where(_.id === id).delete
}
