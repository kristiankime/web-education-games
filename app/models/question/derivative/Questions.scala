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

  def quiz(implicit session: Session) = Questions.quizFor(id)

  def forWho(implicit session: Session) = Questions.forWho(id)

  def answersAndOwners(implicit session: Session) = Questions.answersAndOwners(id)

}

object Questions {

  // ======= CREATE ======
  def create(info: Question, quizId: QuizId)(implicit session: Session) : Question = {
    val questionId = (questionsTable returning questionsTable.map(_.id)) += info
    quizzesQuestionsTable += Quiz2Question(quizId, questionId)
    info.copy(id = questionId)
  }

  def create(info: Question, groupId: GroupId, quizId: QuizId, userId: UserId)(implicit session: Session) : Question = {
    val question = create(info, quizId)
    questionsForTable += QuestionFor(groupId, question.id, userId)
    question
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

  def quizFor(questionId: QuestionId)(implicit session: Session) = {
    (for(
      q2q <- quizzesQuestionsTable if q2q.questionId === questionId;
        q <- quizzesTable if q.id === q2q.quizId
    ) yield q).firstOption
  }

  def forWho(questionId: QuestionId)(implicit session: Session) = {
    (for(
      q4 <- questionsForTable if q4.questionId === questionId;
      u <- UserTable.userTable if u.id === q4.userId
    ) yield u).firstOption
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = quizzesQuestionsTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete

}
