package models.question.derivative

import mathml._
import mathml.scalar._
import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import models.organization.table._
import models.question.derivative.table._
import service.User
import models.id._
import org.joda.time.DateTime
import service._

case class Quiz(id: QuizId, name: String, creationDate: DateTime, updateDate: DateTime)

case class QuizTmp(name: String, date: DateTime){
	def apply(id: QuizId) = Quiz(id, name, date, date)
}

object Quizzes {

	def create(owner: User, info: QuizTmp, courseId: Option[CourseId]) = DB.withSession { implicit session: Session =>
		val quizId = QuizzesTable.insert(info)
		UsersQuizzesTable.insert(User2Quiz(owner.id, quizId, Own))
		courseId.foreach(c => { CoursesQuizzesTable.insert(Course2Quiz(c, quizId)) })
		quizId
	}
	
	// ======
	
	def findByCourse(courseId: CourseId) = DB.withSession { implicit session: Session =>
		(for (
			q <- QuizzesTable;
			cq <- CoursesQuizzesTable if cq.quizId === q.id
		) yield q).list
	}

	def createQuiz(creator: User, name: String, questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		val now = DateTime.now
		val quizId = QuizzesTable.autoInc.insert(name, now, now)
		QuizzesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quizId, _)): _*)
		quizId
	}

	def allQuizzes() = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).list
	}

	def findQuiz(quizId: QuizId) = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).where(_.id === quizId).firstOption
	}

	def findQuizzes(quizIds: List[QuizId]) = DB.withSession { implicit session: Session =>
		Query(QuizzesTable).where(_.id inSet quizIds.toSet).firstOption
	}

	def findQuestionIds(quizId: QuizId) = DB.withSession { implicit session: Session =>
		Query(QuizzesQuestionsTable).where(_.quizId === quizId).list.map(_.questionId)
	}

	def findQuestions(quizId: QuizId) = DB.withSession { implicit session: Session =>
		(for {
			l <- QuizzesQuestionsTable if l.quizId === quizId
			q <- QuestionsTable if l.questionId === q.id
		} yield q).list
	}

	def findQuizAndQuestions(quizId: QuizId) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestions(quizId)) }
	}

	def findQuizAndQuestionIds(quizId: QuizId) = DB.withSession { implicit session: Session =>
		findQuiz(quizId).map { (_, findQuestionIds(quizId).map(_.v)) }
	}

	def updateQuiz(quiz: Quiz, questionIds: List[QuestionId]) = DB.withSession { implicit session: Session =>
		QuizzesTable.where(_.id === quiz.id).update(quiz)
		// LATER do this more efficiently
		QuizzesQuestionsTable.where(_.quizId === quiz.id).delete
		QuizzesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quiz.id, _)): _*)
	}

	def deleteQuiz(id: QuizId) = DB.withSession { implicit session: Session =>
		QuizzesTable.where(_.id === id).delete
	}

}
