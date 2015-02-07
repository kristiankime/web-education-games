package models.quiz.question

import com.artclod.tuple._
import models.quiz._
import models.quiz.table._
import models.support._
import models.user.User
import play.api.db.slick.Config.driver.simple._

import scala.language.postfixOps

object Questions {

  // ======= FIND ======
  def list()(implicit session: Session) : List[Question] =
    questionTables.->(_.list, _.list).map(v => v._1 ++ v._2)

  def apply(questionId: QuestionId)(implicit session: Session) : Option[Question] =
    questionTables.->(_.where(_.id === questionId).firstOption, _.where(_.id === questionId).firstOption).map(v => v._1 ++ v._2 headOption)

  def correctResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.correctResults(user, num, t.question, t.answer),
      t => TangentQuestions.correctResults(user, num, t.question, t.answer) ).map(v => v._1 ++ v._2)

  def incorrectResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.incorrectResults(user, num, t.question, t.answer),
      t => TangentQuestions.incorrectResults(user, num, t.question, t.answer) ).map(v => v._1 ++ v._2)

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = {
    question match {
      case q: DerivativeQuestion => DerivativeQuestions.remove(quiz, q)
      case q: TangentQuestion => TangentQuestions.remove(quiz, q)
    }
  }


}

