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
import models.question.derivative.table.QuizzesQuestionsTable
import models.question.derivative.table.Quiz2Question
import models.question.derivative.table.AnswersTable

case class Question(id: QuestionId, mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime)

case class QuestionTmp(mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime) {
	def apply(id: QuestionId) = Question(id, mathML, rawStr, synched, creationDate)
}

object Questions {

	def find(questionId: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).where(_.id === questionId).firstOption
	}

	def findAnswers(qid: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.questionId === qid).list
	}
		
	def create(owner: User, info: QuestionTmp, quiz: QuizId) = DB.withSession { implicit session: Session =>
		val questionId = (new QuestionsTable).insert(info)
		(new UsersQuestionsTable).insert(owner, questionId)
		(new QuizzesQuestionsTable).insert(Quiz2Question(quiz, questionId))
		info(questionId)
	}

	// ==========

	def allQuestions() = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).list
	}

	def createQuestion(owner: User, mathML: MathMLElem, rawStr: String, synched: Boolean): QuestionId = DB.withSession { implicit session: Session =>
		val qid = (new QuestionsTable).autoInc.insert(mathML, rawStr, synched, DateTime.now)
		(new UsersQuestionsTable).insert(owner, qid)
		qid
	}

	def findQuestion(questionId: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).where(_.id === questionId).firstOption
	}

	def findQuestions(questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).where(_.id inSet questionIds).list
	}

	def findQuestionsForUser(userId: UserId) = DB.withSession { implicit session: Session =>
		(for {
			uq <- (new UsersQuestionsTable) if uq.userId === userId
			q <- (new QuestionsTable) if uq.questionId === q.id
		} yield q).list
	}

	def deleteQuestion(id: QuestionId) = DB.withSession { implicit session: Session =>
		(new QuestionsTable).where(_.id === id).delete
	}
}
