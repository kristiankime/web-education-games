package models.question.derivative

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import models.question.derivative.table._

case class Quiz(id: Long, name: String)

object Quizes {

	def createQuiz(name: String, questionIds: List[Long]) = DB.withSession { implicit session: Session =>
		val quizId = QuizesTable.autoInc.insert(name)
		QuizesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quizId, _)): _*)
		quizId
	}

	def allQuizes() = DB.withSession { implicit session: Session =>
		Query(QuizesTable).list
	}

	def findQuiz(quizId: Long) = DB.withSession { implicit session: Session =>
		Query(QuizesTable).where(_.id === quizId).firstOption
	}

	def findQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
		Query(QuizesQuestionsTable).where(_.quizId === quizId).list.map(_.questionId)
	}

	def findQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
		(for {
			l <- QuizesQuestionsTable if l.quizId === quizId
			q <- QuestionsTable if l.questionId === q.id
		} yield q).list
	}

	def findQuizAndQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestions(quizId)) }
	}

	def findQuizAndQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestionIds(quizId)) }
	}

	def updateQuiz(quiz: Quiz, questionIds: List[Long]) = DB.withSession { implicit session: Session =>
		QuizesTable.where(_.id === quiz.id).update(quiz)
		// LATER do this more efficiently
		QuizesQuestionsTable.where(_.quizId === quiz.id).delete
		QuizesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quiz.id, _)): _*)
	}

	def deleteQuiz(id: Long) = DB.withSession { implicit session: Session =>
		QuizesTable.where(_.id === id).delete
	}

}
