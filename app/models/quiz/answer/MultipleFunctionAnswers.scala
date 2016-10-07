package models.quiz.answer

import com.artclod.mathml.Yes
import com.artclod.mathml.{MathML, Match, MathMLEq}
import com.artclod.mathml.scalar.MathMLElem
import controllers.quiz.multiplefunction.MultipleFunctionAnswerForm
import models.quiz.question.{MultipleFunctionQuestion, MultipleChoiceQuestion}
import models.quiz.table.{AnswerIdNext, multipleFunctionAnswersTable, multipleFunctionAnswerOptionsTable}
import models.support.{AnswerId, QuestionId}
import play.api.db.slick.Config.driver.simple._

object MultipleFunctionAnswers {

  def apply(answerId: AnswerId)(implicit session: Session) = {
    multipleFunctionAnswerOptionsTable.where( r => r.answerId === answerId ).list
  }

  def answerOptions(question: MultipleFunctionQuestion, form: MultipleFunctionAnswerForm)(implicit session: Session) =
    com.artclod.tuple.listZip(question.answerOptions, form.functionGuessesStr, form.functionGuesses).map(e => {
      val math = MathML(e._3).get
      val (ans, str, _) = e
      val correct = MathMLEq.checkEq("x", ans.functionMath, math)
      MultipleFunctionAnswerOption(-1, null, math, str, correct.num)
    })

  def correct(question: MultipleFunctionQuestion, guessFunctions: List[MathMLElem])(implicit session: Session) : Match = {
    val optionsAndAnswers = question.answerOptions.zip(guessFunctions)
    val corrects = optionsAndAnswers.map( a =>  MathMLEq.checkEq("x", a._1.functionMath, a._2))
    val allCorrect = corrects.fold(Yes)( MathMLEq.matchCombine )
    allCorrect
  }

  // ======= CREATE ======
  def createAnswer(answer: MultipleFunctionAnswer, options: List[MultipleFunctionAnswerOption])(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    multipleFunctionAnswersTable += toInsert
    multipleFunctionAnswerOptionsTable ++= options.map(_.copy(answerId = toInsert.id))
    toInsert
  }

}
