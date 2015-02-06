package models.quiz.answer

import com.artclod.tuple._
import models.quiz._
import models.quiz.table._
import models.support._
import play.api.db.slick.Config.driver.simple._

import scala.language.postfixOps

object Answers {

  // ======= FIND ======
  def list()(implicit session: Session) : List[Answer] =
    answerTables.->(_.list, _.list).map(v => v._1 ++ v._2)

  def apply(answerId: AnswerId)(implicit session: Session) : Option[Answer] =
    answerTables.->(_.where(_.id === answerId).firstOption, _.where(_.id === answerId).firstOption).map(v => v._1 ++ v._2 headOption)

}

