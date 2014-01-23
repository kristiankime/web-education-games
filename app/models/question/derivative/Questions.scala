package models.question.derivative

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import service.User
import play.api.db.slick.DB
import play.api.Play.current
import models.question.derivative.table.QuestionsTable
import models.question.derivative.table.UsersQuestionsTable
import models.id._
import models.id._
import org.joda.time.DateTime

case class Question(id: QuestionId, mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime)

case class QuestionTmp(mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime) {
	def apply(id: QuestionId) = Question(id, mathML, rawStr, synched, creationDate)
}

object Questions {

	def create(owner: User, info: QuestionTmp) = DB.withSession { implicit session: Session =>
		val qid = QuestionsTable.insert(info)
		UsersQuestionsTable.insert(owner, qid)
		info(qid)
	}

	// ==========

	def allQuestions() = DB.withSession { implicit session: Session =>
		Query(QuestionsTable).list
	}

	def createQuestion(owner: User, mathML: MathMLElem, rawStr: String, synched: Boolean): QuestionId = DB.withSession { implicit session: Session =>
		val qid = QuestionsTable.autoInc.insert(mathML, rawStr, synched, DateTime.now)
		UsersQuestionsTable.insert(owner, qid)
		qid
	}

	def findQuestion(questionId: QuestionId) = DB.withSession { implicit session: Session =>
		Query(QuestionsTable).where(_.id === questionId).firstOption
	}

	def findQuestions(questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		Query(QuestionsTable).where(_.id inSet questionIds).list
	}

	def findQuestionsForUser(userId: UserId) = DB.withSession { implicit session: Session =>
		(for {
			uq <- UsersQuestionsTable if uq.userId === userId
			q <- QuestionsTable if uq.questionId === q.id
		} yield q).list
	}

	def deleteQuestion(id: QuestionId) = DB.withSession { implicit session: Session =>
		QuestionsTable.where(_.id === id).delete
	}
}
