package models.organization

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

import mathml._
import mathml.scalar._
import models.question.derivative.table._
import models.organization.table._
import service._

case class Course(id: Long, name: String)

case class CourseTmp(name: String) {
	def apply(id: Long) = { Course(id, name) }
}

object Courses {

	def coursesAndEnrollment(implicit user: User) = DB.withSession { implicit session: Session =>
		(for {
			c <- CoursesTable
			uc <- UsersCoursesTable if c.id === uc.courseId && uc.userId === user.uid
		} yield (c, uc != null)).list

	}

	def createCourse(teacher: User, courseInfo: CourseTmp, quizes: Long*) = DB.withSession { implicit session: Session =>
		val courseId = CoursesTable.insert(courseInfo)

		UsersCoursesTable.insert(User2Course(teacher.uid, courseId))

		UsersCoursesTable.insertAll(quizes.map(User2Course(courseId, _)): _*)
		courseId
	}
	//
	//	def allQuizzes() = DB.withSession { implicit session: Session =>
	//		Query(QuizesTable).list
	//	}
	//
	//	def findQuiz(quizId: Long) = DB.withSession { implicit session: Session =>
	//		Query(QuizesTable).where(_.id === quizId).firstOption
	//	}
	//
	//	def findQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
	//		Query(QuizzesQuestionsTable).where(_.quizId === quizId).list.map(_.questionId)
	//	}
	//
	//	def findQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
	//		(for {
	//			l <- QuizzesQuestionsTable if l.quizId === quizId
	//			q <- QuestionsTable if l.questionId === q.id
	//		} yield q).list
	//	}
	//
	//	def findQuizAndQuestions(quizId: Long) = DB.withSession { implicit session: Session =>
	//		findQuiz(quizId).map { (_, findQuestions(quizId)) }
	//	}
	//
	//	def findQuizAndQuestionIds(quizId: Long) = DB.withSession { implicit session: Session =>
	//		findQuiz(quizId).map { (_, findQuestionIds(quizId)) }
	//	}
	//
	//	def updateQuiz(quiz: Quiz, questionIds: List[Long]) = DB.withSession { implicit session: Session =>
	//		QuizesTable.where(_.id === quiz.id).update(quiz)
	//		// LATER do this more efficiently
	//		QuizzesQuestionsTable.where(_.quizId === quiz.id).delete
	//		QuizzesQuestionsTable.insertAll(questionIds.map(Quiz2Question(quiz.id, _)): _*)
	//	}
	//
	//	def deleteQuiz(id: Long) = DB.withSession { implicit session: Session =>
	//		QuizesTable.where(_.id === id).delete
	//	}

}
