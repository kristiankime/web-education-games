package models.question.derivative

import play.api.db.slick.Config.driver.simple._
import models.organization.table._
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import service._
import models.organization._
import viewsupport.question.derivative._

case class QuizTmp(owner: UserId, name: String, date: DateTime) {
	def apply(id: QuizId) = Quiz(id, owner, name, date, date)
}

case class Quiz(id: QuizId, owner: UserId, name: String, creationDate: DateTime, updateDate: DateTime) extends Secured {
	
	def results(student: User)(implicit session: Session) = 
		UserQuizResults(student, this, questions.map(v => v.results(student)))
	
	def questions(implicit session: Session) = Quizzes.findQuestions(id)
		
	def results(section: Section)(implicit session: Session) : QuizResults = QuizResults(this, section.results(this))
	
	protected def linkAccess(implicit user: User, session: Session) = Quizzes.linkAccess(this)
	
	def access(implicit user: User, session: Session) = 
		(Quizzes.findRelatedCourses(id).map(_.access.ceilEdit) :+ directAccess) max
		
}

object Quizzes {

	// ======= CREATE ======
	def create(info: QuizTmp, courseId: Option[CourseId])(implicit session: Session) = {
		val quizId = (new QuizzesTable).insert(info)
//		(new UsersQuizzesTable).insert(User2Quiz(info.owner, quizId, Own))
		courseId.foreach(c => { (new CoursesQuizzesTable).insert(Course2Quiz(c, quizId)) })
		info(quizId)
	}

	// ======= FIND ======
	def find(quizId: QuizId)(implicit session: Session) =
		Query(new QuizzesTable).where(_.id === quizId).firstOption

	def findQuestions(quizId: QuizId)(implicit session: Session) = 
		(for {
			l <- (new QuizzesQuestionsTable) if l.quizId === quizId
			q <- (new QuestionsTable) if l.questionId === q.id
		} yield q).list
	

	def findByCourse(courseId: CourseId)(implicit session: Session) = 
		(for (
			q <- (new QuizzesTable);
			cq <- (new CoursesQuizzesTable) if cq.quizId === q.id && cq.courseId === courseId
		) yield q).list

	def findRelatedCourses(quizId: QuizId)(implicit session: Session) = 
		(for (
			c <- (new CoursesTable);
			cq <- (new CoursesQuizzesTable) if cq.courseId === c.id && cq.quizId === quizId
		) yield c).list
		
	// ======= UPDATE ======
	def rename(quizId: QuizId, name: String)(implicit session: Session) = 
		Query(new QuizzesTable).where(_.id === quizId).firstOption match {
			case Some(quiz) => { Query(new QuizzesTable).where(_.id === quizId).update(quiz.copy(name = name)); true }
			case None => false
		}
	
	// ======= AUTHORIZATION ======
	def linkAccess(quiz: Quiz)(implicit user: User, session: Session) =
		Query(new UsersQuizzesTable).where(uq => uq.userId === user.id && uq.id === quiz.id).firstOption.map(_.access).toAccess

}
