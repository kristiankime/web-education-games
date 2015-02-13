package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.quiz._
import models.quiz.answer.table.DerivativeAnswersTable
import models.quiz.answer.{DerivativeAnswer, DerivativeAnswers, TestDerivativeAnswer}
import models.quiz.question.table.DerivativeQuestionsTable
import models.quiz.table.{derivativeAnswersTable, derivativeQuestionsTable}
import models.support.QuestionId
import models.user.User
import org.joda.time.{DateTime, DateTimeZone}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._
import service._


@RunWith(classOf[JUnitRunner])
class QuestionSpec extends Specification {


  "results" should {
    "find all the answers by the user and record false if all of the answers were wrong" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id))
        val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id), quiz.id)

        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = false))
        val answer2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = false))

        question.results(user) must beEqualTo(DerivativeQuestionResults(user, question, List(answer1, answer2)))
      }
    }

    "find all the answers by the user and record true if any of the answers were correct" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id))
        val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id), quiz.id)

        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = false))
        val answer2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = true))

        question.results(user) must beEqualTo(DerivativeQuestionResults(user, question, List(answer1, answer2)))
      }
    }
  }


  "answers" should {
    "find all the answers by the user" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = DerivativeQuestion(null, user.id, x + `1`, "x + 1", JodaUTC.now, 0d)
        val question = DerivativeQuestions.create(qTmp, quiz.id)

        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))
        val answer2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }

    "not find answers by other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = DerivativeQuestion(null, user.id, x + `1`, "x + 1", JodaUTC.now, 0d)
        val question = DerivativeQuestions.create(qTmp, quiz.id)

        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))
        val answer2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))

        val otherAnswer = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = DBTest.newFakeUser(UserTest()).id, questionId = question.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }

    "not find answers to other questions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = DerivativeQuestion(null, user.id, x + `1`, "x + 1", JodaUTC.now, 0d)
        val question = DerivativeQuestions.create(qTmp, quiz.id)
        val otherQuestion = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id), quiz.id)

        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))
        val answer2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id))

        val otherAnswer = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = otherQuestion.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }
  }

  private def d(l: Long) = JodaUTC(l)

  private def questionAndAnswers(user: User, answers: (Boolean, DateTime)* )(implicit session: Session) = {
    val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
    for(answer <- answers) { DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = answer._1, creationDate = answer._2)) }
    question
  }

}