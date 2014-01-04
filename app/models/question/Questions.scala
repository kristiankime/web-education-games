package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.mapper.MathMLMapper._
import models.security.User
import models.question.table.UserQuestionsTable
import models.question.table._

case class Question(id: Long, mathML: MathMLElem, rawStr: String, synched: Boolean)

object Questions {
	def all()(implicit s: Session) = Query(QuestionsTable).list

	def create(owner: User, mathML: MathMLElem, rawStr: String, synched: Boolean)(implicit s: Session): Long = {
		val qid = QuestionsTable.autoInc.insert(mathML, rawStr, synched)
		UserQuestionsTable.create(owner, qid)
		qid
	}

	def read(id: Long)(implicit s: Session) = Query(QuestionsTable).where(_.id === id).firstOption

	def read(ids: List[Long])(implicit s: Session) = Query(QuestionsTable).where(_.id inSet ids).list

	def delete(id: Long)(implicit s: Session) = QuestionsTable.where(_.id === id).delete
}
