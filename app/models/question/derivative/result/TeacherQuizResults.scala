package models.question.derivative.result

import models.question.derivative.Quiz
import play.api.db.slick.Config.driver.simple.Session

case class TeacherQuizResults(quiz: Quiz, results: List[QuestionResults]) extends QuizResults {

//  def teacherScore(implicit session: Session): Option[Double] = {
//    val teacherScores = results.flatMap(_.teacherScore)
//    if (teacherScores.isEmpty) None
//    else Some(teacherScores.sum / teacherScores.size)
//  }

}
