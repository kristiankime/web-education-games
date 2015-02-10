package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.quiz._
import models.quiz.answer.result.{DerivativeQuestionResults, DerivativeQuestionScores}
import models.quiz.answer.table.DerivativeAnswersTable
import models.quiz.answer.{DerivativeAnswer, DerivativeAnswers, TestDerivativeAnswer}
import models.quiz.question.table.DerivativeQuestionsTable
import models.quiz.table._
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
class QuestionsSpec extends Specification {

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

  "Questions" should {
    "create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
        val qTmp = DerivativeQuestion(null, user.id, x + `1`, "x + 1", JodaUTC.now, 0d)
        val question = DerivativeQuestions.create(qTmp, quiz.id)
        val eq = DerivativeQuestions(question.id)

        eq must beSome(question)
      }
    }

    "return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))

        DerivativeQuestions.create(DerivativeQuestion(null, user.id, x + `1`, "x + 2", JodaUTC.now, 0d), quiz.id)
        DerivativeQuestions.create(DerivativeQuestion(null, user.id, x + `2`, "x + 2", JodaUTC.now, 0d), quiz.id)

        val eqs = DerivativeQuestions.list.map(_.mathML)
        eqs must beEqualTo(List(x + `1`, x + `2`))
      }
    }

    "return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val eq = DerivativeQuestions(QuestionId(Int.MaxValue))

        eq must beNone
      }
    }

  }

  "summary" should {

    "be empty if the user has never answers a question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser

        DerivativeQuestions.summary(user) must beEmpty
      }
    }

    "return answer if there is one" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = DBTest.newFakeUser.id, mathML = x, rawStr = "x"))

        val user = DBTest.newFakeUser
        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        DerivativeQuestions.summary(user) must beEqualTo(List(DerivativeQuestionScores(question1.id, 1, question1.mathML, question1.rawStr, false, JodaUTC(1))))
      }
    }

    "collapse all answers for one question into a row" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = DBTest.newFakeUser.id, mathML = x, rawStr = "x"))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        DerivativeQuestions.summary(user) must beEqualTo(List(DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, JodaUTC(1))))
      }
    }

    "not count answers from other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = DBTest.newFakeUser.id, mathML = x, rawStr = "x"))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))

        val user2 = DBTest.newFakeUser
        val answer = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user2.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))

        DerivativeQuestions.summary(user) must beEqualTo(List(DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, JodaUTC(1))))
      }
    }

    "return one row per question" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>

        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = DBTest.newFakeUser.id, mathML = x, rawStr = "x"))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = DBTest.newFakeUser.id, mathML = x, rawStr = "x"))

        val user = DBTest.newFakeUser
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(1)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(3)))

        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(2)))

        DerivativeQuestions.summary(user) must beEqualTo(List(
          DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, JodaUTC(1)),
          DerivativeQuestionScores(question2.id, 1, question2.mathML, question2.rawStr, false, JodaUTC(2))
        ))
      }
    }
  }

  "summary" should {

    "find nothing if user has never answered any questions " in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())

        DerivativeQuestions.summary(user) must beEmpty
      }
    }

    "find one answer if that's all the user has done" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = false))

        DerivativeQuestions.summary(user) must beEqualTo(List(DerivativeQuestionScores(question.id, 1, question.mathML, question.rawStr, false, answer1.creationDate)))
      }
    }

    "find multiple answers to multiple questions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        DerivativeQuestions.summary(user) must beEqualTo(List(
          DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, answer1_1.creationDate),
          DerivativeQuestionScores(question2.id, 1, question2.mathML, question2.rawStr,false, answer2_1.creationDate)
        ))
      }
    }

    "ignore answers other users have made" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        DerivativeQuestions.summary(user) must beEqualTo(List(
          DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, answer1_1.creationDate),
          DerivativeQuestionScores(question2.id, 1, question2.mathML, question2.rawStr, false, answer2_1.creationDate)
        ))
      }
    }

    "summarize answers from the specified quiz only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id)) // NOTE: In the quiz
        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id), quiz.id)
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id)) // NOTE: Not in quiz
        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        DerivativeQuestions.summary(user, None, Some(quiz)) must beEqualTo(List(DerivativeQuestionScores(question1.id, 2, question1.mathML, question1.rawStr, true, answer1_1.creationDate)))
      }
    }

    "summarize answers before asOf date only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        DerivativeQuestions.summary(user, Some(JodaUTC(1))) must beEqualTo(List(
          DerivativeQuestionScores(question1.id, 1, question1.mathML, question1.rawStr, false, answer1_1.creationDate),
          DerivativeQuestionScores(question2.id, 1, question2.mathML, question2.rawStr, false, answer2_1.creationDate)
        ))
      }
    }

    "summarize answers from the specified quiz and before asOf date only" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id)) // NOTE: In the quiz
        val question1 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id), quiz.id)
        val answer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))
        val answer1_2 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question1.id, correct = true, creationDate = JodaUTC(2)))
        val question2 = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id)) // NOTE: Not in quiz
        val answer2_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question2.id, correct = false, creationDate = JodaUTC(1)))

        val otherUser = DBTest.newFakeUser(UserTest())
        val otherAnswer1_1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = otherUser.id, questionId = question1.id, correct = false, creationDate = JodaUTC(0)))

        DerivativeQuestions.summary(user, Some(JodaUTC(1)), Some(quiz)) must beEqualTo(List(DerivativeQuestionScores(question1.id, 1, question1.mathML, question1.rawStr, false, answer1_1.creationDate)))
      }
    }

  }

  "correct" should {
    "find nothing if user has never answered any questions (even if other users have)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())

        val otherUser = DBTest.newFakeUser(UserTest())
        questionAndAnswers(otherUser, (true, d(0)))

        Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEmpty
      }
    }

    "find nothing if user has never answered a question correctly" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        questionAndAnswers(user, (false, d(0)))

        Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEmpty
      }
    }

    "find correct question if user has answered it" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (true, d(0)))

        Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q1.id, d(0))))
      }
    }

    "find correct question and time if user has answered multiple times" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (false, d(0)), (true, d(1)), (false, d(2)), (true, d(3)) )

        Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q1.id, d(1))))
      }
    }

    "find correct questions if user has answered multiple (ordered by most recent first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (true, d(4)) )
        val q2 = questionAndAnswers(user, (false, d(0)), (true, d(1)), (false, d(2)), (true, d(7)) )

        Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q1.id, d(4)), (q2.id, d(1))))
      }
    }
  }

  "incorrect" should {
    "find nothing if user has never answered any questions (even if other users have)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())

        val otherUser = DBTest.newFakeUser(UserTest())
        questionAndAnswers(otherUser, (true, d(0)))

        Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEmpty
      }
    }

    "find result if user has only answered a question incorrectly" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (false, d(0)))

        Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q1.id, d(0))))
      }
    }

    "find nothing if user has ever answered it correctly" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        questionAndAnswers(user, (false, d(0)), (true, d(1)))

        Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEmpty
      }
    }

    "date should be most recent if user has answered incorrectly multiple times" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (false, d(0)), (false, d(1)), (false, d(2)), (false, d(3)) )

        Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q1.id, d(3))))
      }
    }

    "find correct questions if user has answered multiple (ordered by most recent first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val q1 = questionAndAnswers(user, (false, d(4)) )
        val q2 = questionAndAnswers(user, (false, d(0)), (false, d(1)), (false, d(2)), (false, d(7)) )

        Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, derivativeQuestionsTable, derivativeAnswersTable) must beEqualTo(List((q2.id, d(7)), (q1.id, d(4))))
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