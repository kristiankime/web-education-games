package models.question

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import models.question.table._

case class Quiz(id: Long, name: String)

object Quizes {

	def createQuiz(name: String, questionIds: List[Long])(implicit s: Session) = {
		val quizId = QuizesTable.autoInc.insert(name)
		QuizQuestionsTable.insertAll(questionIds.map(Question2Quiz(_, quizId)): _*)
		quizId
	}

	def allQuizes()(implicit s: Session) = Query(QuizesTable).list

	def findQuiz(quizId: Long)(implicit s: Session) = Query(QuizesTable).where(_.id === quizId).firstOption

	def findQuestionIds(quizId: Long)(implicit s: Session) = Query(QuizQuestionsTable).where(_.questionSetId === quizId).list.map(_.questionId)

	def findQuestions(quizId: Long)(implicit s: Session) =
		(for {
			l <- QuizQuestionsTable if l.questionSetId === quizId
			q <- QuestionsTable if l.questionId === q.id
		} yield q).list

	def findQuizAndQuestions(quizId: Long)(implicit s: Session) = findQuiz(quizId).map { (_, findQuestions(quizId)) }

	def findQuizAndQuestionIds(quizId: Long)(implicit s: Session) = findQuiz(quizId).map { (_, findQuestionIds(quizId)) }

	def updateQuiz(quiz: Quiz, questionIds: List[Long])(implicit s: Session) = {
		QuizesTable.where(_.id === quiz.id).update(quiz)
		// LATER do this more efficiently
		QuizQuestionsTable.where(_.questionSetId === quiz.id).delete
		QuizQuestionsTable.insertAll(questionIds.map(Question2Quiz(_, quiz.id)): _*)
	}

	def deleteQuiz(id: Long)(implicit s: Session) = QuizesTable.where(_.id === id).delete

}
