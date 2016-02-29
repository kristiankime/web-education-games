package models.quiz.answer.result

import models.quiz.answer.TestDerivativeAnswer
import models.quiz.question.DerivativeDifficulty.Diff
import models.quiz.question.{DerivativeQuestionResults, DerivativeDifficulty, QuestionScoring, TestDerivativeQuestion}
import models.user.UserSettingTest
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class DerivativeQuestionResultsSpec extends Specification {

	"studentPoint" should {

		"be 0 if the question was never answered" in {
        val (answerer, asker) = (UserSettingTest(name = "answerer"), UserSettingTest(name = "asker"))
        val question = TestDerivativeQuestion(owner = asker.id)
        val results = DerivativeQuestionResults(answerer, question, List())

        results.studentPoints must beEqualTo(0)
    }

    "be 'max points' if the question was answered correctly" in {
      val (answerer, asker) = (UserSettingTest(name = "answerer"), UserSettingTest(name = "asker"))
      val question = TestDerivativeQuestion(owner = asker.id)
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.studentPoints must beEqualTo(QuestionScoring.pointsPerQuestion)
    }

  }

  "teacherPoints" should {

    "be 0 for a high difficulty question if the question was never answered" in {
      val (answerer, asker) = (UserSettingTest(name = "answerer"), UserSettingTest(name = "asker"))
      val studentSkill = 1d
      val highDiff: Double = highDifficulty(studentSkill)
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDiff), difficulty = highDiff)
      val results = DerivativeQuestionResults(answerer, question, List())

      results.teacherPoints(studentSkill) must beEqualTo(0)
    }

    "be 'max' for a high difficulty question if the question was was answered correctly" in {
      val (answerer, asker) = (UserSettingTest(name = "answerer"), UserSettingTest(name = "asker"))
      val studentSkill = 1d
      val highDiff: Double = highDifficulty(studentSkill)
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(highDiff), difficulty = highDiff)
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherPoints(studentSkill) must beEqualTo(QuestionScoring.pointsPerQuestion)
    }

    "be '1/2 max' for a high medium question if the question was was answered correctly" in {
      val (answerer, asker) = (UserSettingTest(name = "answerer"), UserSettingTest(name = "asker"))
      val studentSkill = 1d
      val medDiff: Double = mediumDifficulty(studentSkill)
      val question = TestDerivativeQuestion(owner = asker.id, mathML = Diff(medDiff), difficulty = medDiff)
      val answer = TestDerivativeAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherPoints(studentSkill) must beEqualTo(QuestionScoring.pointsPerQuestion / 2)
    }
  }

  private def highDifficulty(studentSkill: Double) = studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor

  private def mediumDifficulty(studentSkill: Double) = (studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor) / 2

}
