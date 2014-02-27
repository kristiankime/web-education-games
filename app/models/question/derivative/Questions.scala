package models.question.derivative

import scala.slick.session.Session

import org.joda.time.DateTime

import mathml._
import mathml.scalar._
import models.id._
import models.organization.Course
import models.question.AsciiMathML
import models.question.derivative.table._
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UserTable

case class Question(id: QuestionId, owner: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) extends AsciiMathML with Secured {
	def linkAccess(implicit user: User, session: Session) : Access = null
//		Seq(course.map(_.access).toAccess, Questions.otherAccess(user, id)).max
}

case class QuestionTmp(owner: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) {
	def apply(id: QuestionId) = Question(id, owner, mathML, rawStr, creationDate)
}

object Questions {

	def otherAccess(user: User, questionId: QuestionId)(implicit session: Session) = 
		Query(new UsersQuestionsTable).where(uq => uq.userId === user.id && uq.id === questionId).firstOption.map(_.access).toAccess
	
	def allQuestions()(implicit session: Session) = Query(new QuestionsTable).list

	def find(questionId: QuestionId)(implicit session: Session) = Query(new QuestionsTable).where(_.id === questionId).firstOption

	def findAnswers(qid: QuestionId)(implicit session: Session) = Query(new AnswersTable).where(_.questionId === qid).list

	def findAnswersAndOwners(qid: QuestionId)(implicit session: Session) =
		(for (
			a <- (new AnswersTable) if a.questionId === qid;
			u <- (new UserTable) if u.id === a.owner
		) yield (a, u)).list

	def findAnswers(qid: QuestionId, owner: User)(implicit session: Session) = Query(new AnswersTable).where(r => r.questionId === qid && r.owner === owner.id).list

	def create(info: QuestionTmp, quiz: QuizId)(implicit session: Session) ={
		val questionId = (new QuestionsTable).insert(info)
		(new QuizzesQuestionsTable).insert(Quiz2Question(quiz, questionId))
		info(questionId)
	}

	def remove(quiz: Quiz, question: Question)(implicit session: Session) = (new QuizzesQuestionsTable).where(r => r.questionId === question.id && r.quizId === quiz.id).delete

}
