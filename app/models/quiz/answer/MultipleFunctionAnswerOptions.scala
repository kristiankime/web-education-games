package models.quiz.answer

import models.quiz.question.MultipleFunctionQuestion
import models.quiz.table.multipleFunctionAnswerOptionsTable
import models.support._
import play.api.db.slick.Config.driver.simple._

object MultipleFunctionAnswerOptions {

//  // ======= CREATE ======
//  def create(question: MultipleFunctionQuestion, options: List[MultipleFunctionQuestionOption])(implicit session: Session) = {
//    multipleFunctionQuestionOptionsTable ++= options.map(_.copy(questionId = question.id))
//  }
//
//  // ======= FIND ======
//  def list()(implicit session: Session) = multipleFunctionQuestionOptionsTable.list

  def apply(answer: MultipleFunctionAnswer)(implicit session: Session) = multipleFunctionAnswerOptionsTable.where(_.answerId === answer.id).list


}