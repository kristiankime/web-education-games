package models.question.derivative

import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import models.support._
import models.question.AsciiMathML
import models.question.derivative.table._
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UserTable
import viewsupport.question.derivative.QuestionResults

case class Question(id: QuestionId, owner: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) extends AsciiMathML {

  def answers(user: User)(implicit session: Session) = answersTable.where(a => a.questionId === id && a.owner === user.id).list

  def results(user: User)(implicit session: Session) = QuestionResults(this, answers(user))

}

object Questions {

  // ======= CREATE ======
  def create(info: Question, quiz: QuizId)(implicit session: Session) = {
    val questionId = (questionsTable returning questionsTable.map(_.id)) += info
    info.copy(id = questionId)
  }

  // ======= FIND ======
  def list()(implicit session: Session) = questionsTable.list

  def apply(questionId: QuestionId)(implicit session: Session) = questionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = answersTable.where(r => r.questionId === qid && r.owner === owner.id).list

  def answers(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- answersTable if a.questionId === qid;
      u <- UserTable.userTable if u.id === a.owner
    ) yield (a, u)).sortBy(_._2.lastName).list

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = quizzesQuestionsTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete

}
