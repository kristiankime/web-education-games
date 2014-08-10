package models.question.derivative

import play.api.db.slick.Config.driver.simple._

package object table {

  val answersTable = TableQuery[AnswersTable]
  val answerTimesTable = TableQuery[AnswerTimesTable]
  val questionsTable = TableQuery[QuestionsTable]
  val quizzesQuestionsTable = TableQuery[Quizzes2QuestionsTable]
  val quizzesTable = TableQuery[QuizzesTable]
  val usersAnswersTable = TableQuery[UsersAnswersTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

}
