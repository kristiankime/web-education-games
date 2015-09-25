package models.quiz.answer

import com.artclod.tuple._
import models.quiz.table._
import models.support._
import play.api.db.slick.Config.driver.simple._

import scala.language.postfixOps

object Answers {

  // ======= FIND ======
  def list()(implicit session: Session) : List[Answer] =
    answerTables.->(_.list, _.list, _.list, _.list)
      .toList[Answer](a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]])


  def apply(answerId: AnswerId)(implicit session: Session) : Option[Answer] =
    answerTables.->(
      _.where(_.id === answerId).list,
      _.where(_.id === answerId).list,
      _.where(_.id === answerId).list,
      _.where(_.id === answerId).list)
      .toList[Answer](a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]], a => a.asInstanceOf[List[Answer]])
      .headOption

}
