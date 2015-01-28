package models.quiz

import models.DBTest._
import models.quiz.answer.TestDerivativeAnswer
import models.quiz.answer.result.DerivativeQuestionResults
import models.quiz.question.QuestionDifficulty.Diff
import models.quiz.question.{DerivativeQuestion, QuestionDifficulty, QuestionScoring, TestDerivativeQuestion}
import models.user.UserSetting
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick._
import service.{Login, UserTest}
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import play.api.Play.current

@RunWith(classOf[JUnitRunner])
class QuizResultsSpec extends Specification {

	"studentScore" should {

    "be 0 if none of the questions were answered" in {
      DB.withSession { implicit session: Session =>
        val (answerer, asker) = (newFakeUser, newFakeUser) // (UserTest(firstName = "answerer"), UserTest(firstName = "asker"))

        val quiz = TestQuiz(asker.id)
        val question1 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question1Results = questionResults(answerer, question1, numberOfAnswers = 0)

        val question2 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question2Results = questionResults(answerer, question2, numberOfAnswers = 0)

        val results = QuizResults(answerer, quiz, List(question1Results, question2Results))

        results.studentScore must beEqualTo(0d)
      }
    }

    "be 1 if all of the questions were answered corectly" in {
      DB.withSession { implicit session: Session =>
        val (answerer, asker) = (newFakeUser, newFakeUser)

        val quiz = TestQuiz(asker.id)
        val question1 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question1Results = questionResults(answerer, question1, correct = true, numberOfAnswers = 2)

        val question2 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question2Results = questionResults(answerer, question2, correct = true, numberOfAnswers = 1)

        val results = QuizResults(answerer, quiz, List(question1Results, question2Results))

        results.studentScore must beEqualTo(1d)
      }
    }

    "be .5 if half of the questions were answered corectly" in {
      DB.withSession { implicit session: Session =>
        val (answerer, asker) = (newFakeUser, newFakeUser)

        val quiz = TestQuiz(asker.id)
        val question1 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question1Results = questionResults(answerer, question1, correct = false, numberOfAnswers = 2)

        val question2 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(1d))
        val question2Results = questionResults(answerer, question2, correct = true, numberOfAnswers = 1)

        val results = QuizResults(answerer, quiz, List(question1Results, question2Results))

        results.studentScore must beEqualTo(.5d)
      }
    }

  }

  "teacherScore" should {

    "be 1 if all of the questions were hard and answered correctly" in {
      DB.withSession { implicit session: Session =>
        val (answerer, asker) = (newFakeUser, newFakeUser)
        val studentSkill = 1d

        val quiz = TestQuiz(asker.id)
        val question1 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
        val question1Results = questionResults(answerer, question1, correct = true, numberOfAnswers = 2)

        val question2 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
        val question2Results = questionResults(answerer, question2, correct = true, numberOfAnswers = 1)

        val results = QuizResults(answerer, quiz, List(question1Results, question2Results))

        results.teacherScore(studentSkill) must beEqualTo(1d)
      }
    }


    "be 0 if all of the questions were hard and answered incorrectly" in {
      DB.withSession { implicit session: Session =>
        val (answerer, asker) = (newFakeUser, newFakeUser)
        val studentSkill = 1d

        val quiz = TestQuiz(asker.id)
        val question1 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
        val question1Results = questionResults(answerer, question1, correct = false, numberOfAnswers = 2)

        val question2 = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
        val question2Results = questionResults(answerer, question2, correct = false, numberOfAnswers = 1)

        val results = QuizResults(answerer, quiz, List(question1Results, question2Results))

        results.teacherScore(studentSkill) must beEqualTo(0d)
      }
    }
  }

  private def highDifficulty(studentSkill: Double) = studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor

  private def mediumDifficulty(studentSkill: Double) = (studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor) / 2

  private def questionResults(answerer: UserSetting, question: DerivativeQuestion, correct: Boolean = false, numberOfAnswers : Int = 1) = {
    val answers = for(i <- 1 to numberOfAnswers) yield {
      TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = if(i == numberOfAnswers) correct else false) // question.id is null here but will work for testing
    }
    DerivativeQuestionResults(answerer, question, answers.toList)
  }

}
