package models.quiz

import com.artclod.tuple._
import com.google.common.annotations.VisibleForTesting
import models.organization._
import models.organization.table._
import models.quiz.question.{Questions, Question}
import models.quiz.question.table.{DerivativeQuestion2QuizTable, DerivativeQuestionsTable, QuestionsTable, Question2QuizTable}
import models.quiz.table._
import models.support._
import models.user.User
import play.api.db.slick.Config.driver.simple._
import service._


object Quizzes {

  // ======= CREATE ======
  @VisibleForTesting
  def create(quiz: Quiz)(implicit session: Session) : Quiz = {
    val quizId: QuizId = (quizzesTable returning quizzesTable.map(_.id)) += quiz
    quiz.copy(id = quizId)
  }

  def create(info: Quiz, courseId: CourseId)(implicit session: Session): Quiz = {
    val quiz = create(info)
    coursesQuizzesTable.insert(Course2Quiz(courseId, quiz.id))
    quiz
  }

  // ======= FIND ======
  def apply(quizId: QuizId)(implicit session: Session) : Option[Quiz] = quizzesTable.where(_.id === quizId).firstOption

  def apply(courseId: CourseId)(implicit session: Session) : List[Quiz]  =
    (for (
      q <- quizzesTable;
      cq <- coursesQuizzesTable if cq.quizId === q.id && cq.courseId === courseId
    ) yield q).sortBy(_.creationDate).list

//  def questions(quizId: QuizId)(implicit session: Session) : List[Question] =
//    questionTables.->(
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list,
//      _.filter(_.quizId === quizId).list)
//      .toList[Question](a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]], a => a.asInstanceOf[List[Question]])

  def questions(quizId: QuizId)(implicit session: Session) : List[Question] =
//    (qt: TableQuery[DerivativeQuestionsTable], jt: TableQuery[DerivativeQuestion2QuizTable])
    questionAnd2QuizTables.->(
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list,
      t => (for (q2q <- t._2; q <- t._1 if q2q.quizId === quizId && q2q.questionId === q.id) yield q).list
    ).toList[Question](
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]],
      (a: List[Question]) => a.asInstanceOf[List[Question]]
    )

//  private def questionIds(quizId: QuizId)(implicit session: Session) : List[QuestionId] =
//    question2QuizTables.->(
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list,
//      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q.questionId).list
//    ).toList[QuestionId](
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]],
//      (a: List[QuestionId]) => a.asInstanceOf[List[QuestionId]]
//    )

  def courses(quizId: QuizId)(implicit session: Session) : List[Course] =
    (for (
      c <- coursesTable;
      cq <- coursesQuizzesTable if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).list

  def course(courseId: CourseId, quizId: QuizId)(implicit session: Session) : Option[Course] =
    (for (
      c <- coursesTable if c.id === courseId;
      cq <- coursesQuizzesTable if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).firstOption

  // ======= UPDATE ======
  def rename(quizId: QuizId, name: String)(implicit session: Session) =
    quizzesTable.where(_.id === quizId).firstOption match {
      case Some(quiz) => {
        quizzesTable.where(_.id === quizId).update(quiz.copy(name = name));
        true
      }
      case None => false
    }

  def setQuestions(quizId: QuizId, questionIds: List[QuestionId])(implicit session: Session): Unit = {
    clearQuestions(quizId)
    addQuestions(quizId, questionIds)
  }

  private def clearQuestions(quizId: QuizId)(implicit session: Session) =
    question2QuizTables.->(
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete,
      t => (for (q2q <- t if q2q.quizId === quizId) yield q2q).delete
    )

  private def addQuestions(quizId: QuizId, questionIds: List[QuestionId])(implicit session: Session): Unit =
    for(q <- questionIds.flatMap(id => Questions(id))) { q.attach(quizId) }

  // ======= AUTHORIZATION ======
  def linkAccess(quiz: Quiz)(implicit user: User, session: Session) =
    usersQuizzesTable.where(uq => uq.userId === user.id && uq.quizId === quiz.id).firstOption.map(_.access).toAccess

  // ======= Scoring ======
  def studentScore(quiz: Quiz, user: User)(implicit session: Session) = {
    quiz.questions.map( _.results(user))
  }

}
