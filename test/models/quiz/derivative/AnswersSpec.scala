package models.quiz.derivative

import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.quiz.answer.DerivativeAnswers
import DerivativeAnswers.AnswersSummary
import models.quiz.question.DerivativeQuestions
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._

@RunWith(classOf[JUnitRunner])
class AnswersSpec extends Specification {

  "summary" should {

    "be empty if the user has never answers a question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser

        DerivativeAnswers.summary(user) must beEmpty
      }
    }

    "return answer if there is one" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestQuestion(owner = DBTest.newFakeUser.id))

        val user = DBTest.newFakeUser
        val answer1 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        DerivativeAnswers.summary(user) must beEqualTo(List(AnswersSummary(question1.id, 1, false, JodaUTC(1))))
      }
    }

    "collapse all answers for one question into a row" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestQuestion(owner = DBTest.newFakeUser.id))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        DerivativeAnswers.summary(user) must beEqualTo(List(AnswersSummary(question1.id, 2, true, JodaUTC(1))))
      }
    }

    "not count answers from other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestQuestion(owner = DBTest.newFakeUser.id))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        val user2 = DBTest.newFakeUser
        val answer = DerivativeAnswers.createAnswer(TestAnswer(owner = user2.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        DerivativeAnswers.summary(user) must beEqualTo(List(AnswersSummary(question1.id, 2, true, JodaUTC(1))))
      }
    }

    "return one row per question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestQuestion(owner = DBTest.newFakeUser.id))
        val question2 = DerivativeQuestions.create(TestQuestion(owner = DBTest.newFakeUser.id))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(3)))

        val answer2_1 = DerivativeAnswers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(2)))

        DerivativeAnswers.summary(user) must beEqualTo(List(
          AnswersSummary(question1.id, 2, true, JodaUTC(1)),
          AnswersSummary(question2.id, 1, false, JodaUTC(2))
        ))
      }
    }

  }

}