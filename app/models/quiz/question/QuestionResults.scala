package models.quiz.question

import models.quiz.Status
import models.quiz.answer._
import models.user.User

sealed trait QuestionResults {
  val answerer: User
  val question: Question
  val answers: List[Answer]

  require(answers.forall(_.ownerId == answerer.id), "All the answers must be from the same user")

  def attempted = answers.nonEmpty

  def questionId = question.id

  val correct = answers.foldLeft(false)(_ || _.correct)

  val status = Status(attempted, correct)

  val firstCorrect = answers.find(_.correct)

  def firstAttempt = answers.headOption.map(_.creationDate)

  val numAttemptsToCorrect = {
    val num = answers.indexWhere(_.correct)
    if (num == -1) None else Some(num + 1)
  }

  def studentPointsOp =
    if(!attempted)   None
    else if(correct) Some(QuestionScoring.pointsPerQuestion)
    else             Some(0)

  def studentPoints =
    if(correct) QuestionScoring.pointsPerQuestion
    else        0

  def teacherPoints(studentSkill: Double): Int = QuestionScoring.teacherScore(question, correct, studentSkill)
}

case class DerivativeQuestionResults(answerer: User, question: DerivativeQuestion, answers: List[DerivativeAnswer]) extends QuestionResults

case class DerivativeGraphQuestionResults(answerer: User, question: DerivativeGraphQuestion, answers: List[DerivativeGraphAnswer]) extends QuestionResults

case class TangentQuestionResults(answerer: User, question: TangentQuestion, answers: List[TangentAnswer]) extends QuestionResults

case class GraphMatchQuestionResults(answerer: User, question: GraphMatchQuestion, answers: List[GraphMatchAnswer]) extends QuestionResults

case class PolynomialZoneQuestionResults(answerer: User, question: PolynomialZoneQuestion, answers: List[PolynomialZoneAnswer]) extends QuestionResults

case class MultipleChoiceQuestionResults(answerer: User, question: MultipleChoiceQuestion, answers: List[MultipleChoiceAnswer]) extends QuestionResults

case class MultipleFunctionQuestionResults(answerer: User, question: MultipleFunctionQuestion, answers: List[MultipleFunctionAnswer]) extends QuestionResults
