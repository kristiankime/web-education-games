package models.question.derivative.result

import models.question.derivative.Quiz
import service.User

case class StudentQuizResults(student: User, quiz: Quiz, results: List[QuestionResults]) extends QuizResults {
  require(results.forall(_.answerer == student), "All the question results must be from the same user")

  val score = {
    val scores = results.flatMap(_.score)
    if (scores.isEmpty) None
    else Some(scores.sum / results.size)
  }

  val studentScore = results.map(_.studentScore).sum / results.size

  def teacherScore(studentSkill: Double) = results.map(_.teacherScore(studentSkill)).sum / results.size

}
