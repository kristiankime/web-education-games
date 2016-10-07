package models.quiz.question

import models.quiz.table.multipleFunctionQuestionOptionsTable
import models.support._
import play.api.db.slick.Config.driver.simple._

object MultipleFunctionQuestionOptions {

//  // ======= CREATE ======
//  def create(question: MultipleFunctionQuestion, options: List[MultipleFunctionQuestionOption])(implicit session: Session) = {
//    multipleFunctionQuestionOptionsTable ++= options.map(_.copy(questionId = question.id))
//  }
//
//  // ======= FIND ======
//  def list()(implicit session: Session) = multipleFunctionQuestionOptionsTable.list

  def apply(question: MultipleFunctionQuestion)(implicit session: Session) = multipleFunctionQuestionOptionsTable.where(_.questionId === question.id).list


}