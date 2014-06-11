package models.question.derivative

import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import models.support._
import models.question.AsciiMathML
import models.question.derivative.table._
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UsersTable
import viewsupport.question.derivative.QuestionResults

case class Question(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) extends AsciiMathML with Owned {

  def answers(user: User)(implicit session: Session) = Questions(id, user)

  def results(user: User)(implicit session: Session) = QuestionResults(user, this, answers(user), start(user))

  def start(user: User)(implicit session: Session) = Answers.startedWork(user, id).map(_.time)

  def quiz(implicit session: Session) = Questions.quizFor(id)

  def forWho(implicit session: Session) = Questions.forWho(id)

  def answersAndOwners(implicit session: Session) = Questions.answersAndOwners(id)

  def difficulty(implicit session: Session) : Option[Double] = {
    val difficulties = Questions.answerers(id).flatMap(results(_).score).map(1d - _)
    if (difficulties.isEmpty) None
    else Some(difficulties.sum / difficulties.size)
  }

}

object Questions {

  // ======= CREATE ======
  def create(info: Question, quizId: QuizId)(implicit session: Session): Question = {
    val questionId = (questionsTable returning questionsTable.map(_.id)) += info
    quizzesQuestionsTable += Quiz2Question(quizId, questionId)
    info.copy(id = questionId)
  }

  def create(info: Question, groupId: GroupId, quizId: QuizId, userId: UserId)(implicit session: Session): Question = {
    val question = create(info, quizId)
    questionsForTable += GroupQuestion2User(groupId, question.id, userId)
    question
  }

  // ======= FIND ======
  def list()(implicit session: Session) = questionsTable.list

  def apply(questionId: QuestionId)(implicit session: Session) = questionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = answersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- answersTable if a.questionId === qid;
      u <- UsersTable.userTable if u.id === a.ownerId
    ) yield (a, u)).sortBy(_._2.lastName).list

  def quizFor(questionId: QuestionId)(implicit session: Session) = {
    (for (
      q2q <- quizzesQuestionsTable if q2q.questionId === questionId;
      q <- quizzesTable if q.id === q2q.quizId
    ) yield q).firstOption
  }

  def forWho(questionId: QuestionId)(implicit session: Session) = {
    (for (
      q4 <- questionsForTable if q4.questionId === questionId;
      u <- UsersTable.userTable if u.id === q4.userId
    ) yield u).firstOption
  }

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = answersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.UsersTable.userTable.where(_.id in userIds)
    users.list
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = quizzesQuestionsTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete

}
