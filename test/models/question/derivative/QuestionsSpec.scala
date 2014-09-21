package models.question.derivative

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.support.QuestionId
import org.joda.time.{DateTimeZone, DateTime}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._
import service._
import viewsupport.question.derivative.QuestionResults

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

  "results" should {
    "find all the answers by the user and record false if all of the answers were wrong" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id))
        val question = Questions.create(TestQuestion(owner = user.id), quiz.id)

        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id, correct = false))
        val answer2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id, correct = false))

        question.results(user) must beEqualTo(QuestionResults(user, question, List(answer1, answer2), None))
      }
    }

    "find all the answers by the user and record true if any of the answers were correct" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id))
        val question = Questions.create(TestQuestion(owner = user.id), quiz.id)

        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id, correct = false))
        val answer2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id, correct = true))

        question.results(user) must beEqualTo(QuestionResults(user, question, List(answer1, answer2), None))
      }
    }
  }

  "answers" should {
    "find all the answers by the user" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = Question(null, user.id, x + `1`, "x + 1", JodaUTC.now)
        val question = Questions.create(qTmp, quiz.id)

        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))
        val answer2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }

    "not find answers by other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = Question(null, user.id, x + `1`, "x + 1", JodaUTC.now)
        val question = Questions.create(qTmp, quiz.id)

        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))
        val answer2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))

        val otherAnswer = Answers.createAnswer(TestAnswer(owner = DBTest.newFakeUser(UserTest()).id, questionId = question.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }

    "not find answers to other questions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = Question(null, user.id, x + `1`, "x + 1", JodaUTC.now)
        val question = Questions.create(qTmp, quiz.id)
        val otherQuestion = Questions.create(TestQuestion(owner = user.id), quiz.id)

        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))
        val answer2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id))

        val otherAnswer = Answers.createAnswer(TestAnswer(owner = user.id, questionId = otherQuestion.id))

        question.answers(user) must beEqualTo(List(answer1, answer2))
      }
    }
  }

  "Questions" should {
    "create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = Question(null, user.id, x + `1`, "x + 1", JodaUTC.now)
        val question = Questions.create(qTmp, quiz.id)
        val eq = Questions(question.id)

        eq must beSome(question)
      }
    }

    "return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))

        Questions.create(Question(null, user.id, x + `1`, "x + 2", JodaUTC.now), quiz.id)
        Questions.create(Question(null, user.id, x + `2`, "x + 2", JodaUTC.now), quiz.id)

        val eqs = Questions.list.map(_.mathML)
        eqs must beEqualTo(List(x + `1`, x + `2`))
      }
    }

    "return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val eq = Questions(QuestionId(Int.MaxValue))

        eq must beNone
      }
    }

  }

  "summary" should {

    "be empty if the user has never answers a question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser

        Questions.summary(user) must beEmpty
      }
    }

    "return answer if there is one" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = Questions.create(TestQuestion(owner = DBTest.newFakeUser.id, mathML = x))

        val user = DBTest.newFakeUser
        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        Questions.summary(user) must beEqualTo(List(QuestionSummary(question1.id, 1, x, false, JodaUTC(1))))
      }
    }

    "collapse all answers for one question into a row" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = Questions.create(TestQuestion(owner = DBTest.newFakeUser.id, mathML = x))

        val user = DBTest.newFakeUser
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        Questions.summary(user) must beEqualTo(List(QuestionSummary(question1.id, 2, x, true, JodaUTC(1))))
      }
    }

    "not count answers from other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = Questions.create(TestQuestion(owner = DBTest.newFakeUser.id, mathML = x))

        val user = DBTest.newFakeUser
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        val user2 = DBTest.newFakeUser
        val answer = Answers.createAnswer(TestAnswer(owner = user2.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        Questions.summary(user) must beEqualTo(List(QuestionSummary(question1.id, 2, x, true, JodaUTC(1))))
      }
    }

    "return one row per question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = Questions.create(TestQuestion(owner = DBTest.newFakeUser.id, mathML = x))
        val question2 = Questions.create(TestQuestion(owner = DBTest.newFakeUser.id, mathML = x))

        val user = DBTest.newFakeUser
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(3)))

        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(2)))

        Questions.summary(user) must beEqualTo(List(
          QuestionSummary(question1.id, 2, x, true, JodaUTC(1)),
          QuestionSummary(question2.id, 1, x, false, JodaUTC(2))
        ))
      }
    }
  }

  "summary" should {

    "find nothing if user has never answered any questions " in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())

        Questions.summary(user) must beEmpty
      }
    }

    "find one answer if that's all the user has done" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question = Questions.create(TestQuestion(owner = user.id))
        val answer1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question.id, correct = false))

        Questions.summary(user) must beEqualTo(List(QuestionSummary(question.id, 1, question.mathML, false, answer1.creationDate)))
      }
    }

    "find multiple answers to multiple questions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = Questions.create(TestQuestion(owner = user.id))
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = Questions.create(TestQuestion(owner = user.id))
        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        Questions.summary(user) must beEqualTo(List(
          QuestionSummary(question1.id, 2, question1.mathML, true, answer1_1.creationDate),
          QuestionSummary(question2.id, 1, question2.mathML, false, answer2_1.creationDate)
        ))
      }
    }

    "ignore answers other users have made" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = Questions.create(TestQuestion(owner = user.id))
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = Questions.create(TestQuestion(owner = user.id))
        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = Answers.createAnswer(TestAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        Questions.summary(user) must beEqualTo(List(
          QuestionSummary(question1.id, 2, question1.mathML, true, answer1_1.creationDate),
          QuestionSummary(question2.id, 1, question1.mathML, false, answer2_1.creationDate)
        ))
      }
    }

    "summarize answers from the specified quiz only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id)) // NOTE: In the quiz
        val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = Questions.create(TestQuestion(owner = user.id)) // NOTE: Not in quiz
        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = Answers.createAnswer(TestAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        Questions.summary(user, quiz) must beEqualTo(List(QuestionSummary(question1.id, 2, question1.mathML, true, answer1_1.creationDate)))
      }
    }

    "summarize answers before asOf date only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = Questions.create(TestQuestion(owner = user.id))
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = Questions.create(TestQuestion(owner = user.id))
        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = Answers.createAnswer(TestAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        Questions.summary(user, JodaUTC(1)) must beEqualTo(List(
          QuestionSummary(question1.id, 1, question1.mathML, false, answer1_1.creationDate),
          QuestionSummary(question2.id, 1, question1.mathML, false, answer2_1.creationDate)
        ))
      }
    }

    "summarize answers from the specified quiz and before asOf date only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id)) // NOTE: In the quiz
        val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
        val answer1_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = Questions.create(TestQuestion(owner = user.id)) // NOTE: Not in quiz
        val answer2_1 = Answers.createAnswer(TestAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = Answers.createAnswer(TestAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        Questions.summary(user, JodaUTC(1), quiz) must beEqualTo(List(QuestionSummary(question1.id, 1, question1.mathML, false, answer1_1.creationDate)))
      }
    }

  }
}