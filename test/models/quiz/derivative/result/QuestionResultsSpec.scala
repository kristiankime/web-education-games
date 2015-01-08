package models.quiz.derivative.result

import com.artclod.mathml.scalar._
import models.quiz.answer.result.DerivativeQuestionResults
import models.quiz.{QuestionDifficulty, QuestionScoring}
import models.quiz.derivative._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import service.{UserTest, User}
import QuestionDifficulty.Diff

@RunWith(classOf[JUnitRunner])
class QuestionResultsSpec extends Specification {

	"studentScore" should {

		"be 0 if the question was never answered" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val question = TestQuestion(owner = asker.id)
      val results = DerivativeQuestionResults(answerer, question, List())

      results.studentScore must beEqualTo(0d)
    }

    "be 1 if the question was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val question = TestQuestion(owner = asker.id)
      val answer = TestAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.studentScore must beEqualTo(1d)
    }

  }

  "teacherScore" should {

    "be 0 for a high difficulty question if the question was never answered" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
      val results = DerivativeQuestionResults(answerer, question, List())

      results.teacherScore(studentSkill) must beEqualTo(0d)
    }

    "be 1 for a high difficulty question if the question was was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestQuestion(owner = asker.id, mathML = Diff(highDifficulty(studentSkill)))
      val answer = TestAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherScore(studentSkill) must beEqualTo(1d)
    }

    "be .5 for a high medium question if the question was was answered correctly" in {
      val (answerer, asker) = (UserTest(firstName="answerer"), UserTest(firstName="asker"))
      val studentSkill = 1d
      val question = TestQuestion(owner = asker.id, mathML = Diff(mediumDifficulty(studentSkill)))
      val answer = TestAnswer(owner = answerer.id, questionId = question.id, correct = true) // question.id is null here but will work for testing
      val results = DerivativeQuestionResults(answerer, question, List(answer))

      results.teacherScore(studentSkill) must beEqualTo(.5d)
    }
  }

  private def highDifficulty(studentSkill: Double) = studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor

  private def mediumDifficulty(studentSkill: Double) = (studentSkill * QuestionScoring.zoneOfProximalDevelopmentFactor) / 2

}
