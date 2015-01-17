package models.quiz.answer.result

import models.quiz.answer.TestDerivativeAnswer
import models.quiz.question.QuestionDifficulty.Diff
import models.quiz.question.{QuestionDifficulty, QuestionScoring, TestDerivativeQuestion}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import service.UserTest

@RunWith(classOf[JUnitRunner])
class DerivativeQuestionResultsSpec extends Specification {

	"studentScore" should {

		"be 0 if the question was never answered" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val question = TestDerivativeQuestion(owner = asker.id)
      val results = DerivativeQuestionResults(answerer, question, List())

      results.studentScore must beEqualTo(0d)
    }

    "be 1 if the question was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val question = TestDerivativeQuestion(owner = asker.id)
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.studentScore must beEqualTo(1d)
    }

  }

  "teacherScore" should {

    "be 0 for a high difficulty question if the question was never answered" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
      val results = DerivativeQuestionResults(answerer, question, List())

      results.teacherScore(studentSkill) must beEqualTo(0d)
    }

    "be 1 for a high difficulty question if the question was was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherScore(studentSkill) must beEqualTo(1d)
    }

    "be .5 for a high medium question if the question was was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(mediumDifficulty(studentSkill)))
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherScore(studentSkill) must beEqualTo(.5d)
    }
  }

  private def highDifficulty(studentSkill: Double) = studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor

  private def mediumDifficulty(studentSkill: Double) = (studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor) / 2

}
