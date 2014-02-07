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
import service.table.UserTable
import models.question.AsciiMathML

case class Question(id: QuestionId, owner: UserId, mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime) extends AsciiMathML

case class QuestionTmp(owner: UserId, mathML: MathMLElem, rawStr: String, synched: Boolean, creationDate: DateTime) {
	def apply(id: QuestionId) = Question(id, owner, mathML, rawStr, synched, creationDate)
}

object Questions {

	def allQuestions() = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).list
	}

	def find(questionId: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new QuestionsTable).where(_.id === questionId).firstOption
	}

	def findAnswers(qid: QuestionId) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(_.questionId === qid).list
	}

	def findAnswersAndOwners(qid: QuestionId) = DB.withSession { implicit session: Session =>
		(for (
			a <- (new AnswersTable) if a.questionId === qid;
			u <- (new UserTable) if u.id === a.owner
		) yield (a, u)).list
		//		Query(new AnswersTable).where(_.questionId === qid).list
	}

	def findAnswers(qid: QuestionId, owner: User) = DB.withSession { implicit session: Session =>
		Query(new AnswersTable).where(r => r.questionId === qid && r.owner === owner.id).list
	}

	def create(info: QuestionTmp, quiz: QuizId) = DB.withSession { implicit session: Session =>
		val questionId = (new QuestionsTable).insert(info)
		(new QuizzesQuestionsTable).insert(Quiz2Question(quiz, questionId))
		info(questionId)
	}

}
