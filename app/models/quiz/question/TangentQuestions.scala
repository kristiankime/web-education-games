package models.quiz.question

import models.quiz.Quiz
import models.quiz.table._
import models.support._
import play.api.db.slick.Config.driver.simple._

object TangentQuestions {

  def create(info: TangentQuestion, quizId: QuizId)(implicit session: Session): TangentQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    tangentQuestionsTable += toInsert
    toInsert
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: TangentQuestion)(implicit session: Session) =
    tangentQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))
}

