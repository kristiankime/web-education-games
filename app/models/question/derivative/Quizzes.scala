package models.question.derivative

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import models.question.derivative.table._
import service.User
import models.question.derivative.table._
import models.id._
import models.id.Ids._

case class Quiz(id: Long, name: String)

object Quizzes {

	def createQuiz(creator: User, name: String, questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		val quizId = QuizzesTable.autoInc.insert(name)
		QuizzesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quizId, _)): _*)
		quizId
	}

	def allQuizzes() = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).list
	}

	def findQuiz(quizId: Long) = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).where(_.id === quizId).firstOption
	}

	def findQuizzes(quizIds: List[Long]) = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).where(_.id inSet quizIds.toSet).firstOption
	}
	
	def findQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
		Query(QuizzesQuestionsTable).where(_.quizId === quizId).list.map(_.questionId)
	}

	def findQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
		(for {
			l <- QuizzesQuestionsTable if l.quizId === quizId
			q <- QuestionsTable if l.questionId === q.id
		} yield q).list
	}

	def findQuizAndQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestions(quizId)) }
	}

	def findQuizAndQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestionIds(quizId).map(_.v)) }
	}

	def updateQuiz(quiz: Quiz, questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		QuizzesTable.where(_.id === quiz.id).update(quiz)
		// LATER do this more efficiently
		QuizzesQuestionsTable.where(_.quizId === quiz.id).delete
		QuizzesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quiz.id, _)): _*)
	}

	def deleteQuiz(id: Long) = DB.withSession { implicit session: Session =>
		QuizzesTable.where(_.id === id).delete
	}

}
