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

case class Quiz(id: QuizId, owner: UserId, name: String, creationDate: DateTime, updateDate: DateTime)

case class QuizTmp(owner: UserId, name: String, date: DateTime){
	def apply(id: QuizId) = Quiz(id, owner, name, date, date)
}

object Quizzes {

	def find(quizId: QuizId) = DB.withSession { implicit session: Session =>
		Query(new QuizzesTable).where(_.id === quizId).firstOption
	}
	
	def findQuestions(quizId: QuizId) = DB.withSession { implicit session: Session =>
		(for {
			l <- (new QuizzesQuestionsTable) if l.quizId === quizId
			q <- (new QuestionsTable) if l.questionId === q.id
		} yield q).list
	}
	
	def create(info: QuizTmp, courseId: Option[CourseId]) = DB.withSession { implicit session: Session =>
		val quizId = (new QuizzesTable).insert(info)
		(new UsersQuizzesTable).insert(User2Quiz(info.owner, quizId, Own))
		courseId.foreach(c => { (new CoursesQuizzesTable).insert(Course2Quiz(c, quizId)) })
		quizId
	}

	def rename(quizId: QuizId, name: String) = DB.withSession { implicit session: Session =>
		Query(new QuizzesTable).where(_.id === quizId).firstOption match {
			case Some(quiz) => { Query(new QuizzesTable).where(_.id === quizId).update(quiz.copy(name = name)); true }
			case None => false
		}
	}
	
	def findByCourse(courseId: CourseId) = DB.withSession { implicit session: Session =>
		(for (
			q <- (new QuizzesTable);
			cq <- (new CoursesQuizzesTable) if cq.quizId === q.id && cq.courseId === courseId
		) yield q).list
	}

}
