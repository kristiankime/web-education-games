package models.quiz.question

import models.quiz.Quiz
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table._
import play.api.db.slick.Config.driver.simple._

object TangentQuestions {

  // ======= CREATE ======
  def create(info: TangentQuestion, quizId: QuizId)(implicit session: Session): TangentQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    tangentQuestionsTable += toInsert
    toInsert
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: TangentQuestion)(implicit session: Session) =
    tangentQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= FIND ======
  def list()(implicit session: Session) = tangentQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = tangentQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = tangentAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = tangentAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- tangentAnswersTable if a.questionId === qid;
      u <- userTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

}

