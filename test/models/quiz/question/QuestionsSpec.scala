package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.quiz._
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
import models.quiz.table.{derivativeAnswersTable, derivativeQuestionsTable}


@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

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


  "results" should {

    "find nothing if user has never answered any questions " in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())

        DerivativeQuestions.results(user)(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEmpty
      }
    }

    "find one answer if that's all the user has done" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
        val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = user.id))
        val answer1 = DerivativeAnswers.createAnswer(TestDerivativeAnswer(owner = user.id, questionId = question.id, correct = false))

        DerivativeQuestions.results(user)(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(DerivativeQuestionResults(user, question, List(answer1))))
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

        DerivativeQuestions.results(user)(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(
          DerivativeQuestionResults(user, question1, List(answer1_1, answer1_2)),
          DerivativeQuestionResults(user, question2, List(answer2_1))
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

        DerivativeQuestions.results(user)(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(
          DerivativeQuestionResults(user, question1, List(answer1_1, answer1_2)),
          DerivativeQuestionResults(user, question2, List(answer2_1))
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

        DerivativeQuestions.results(user, None, Some(quiz))(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(DerivativeQuestionResults(user, question1, List(answer1_1, answer1_2))))
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

        DerivativeQuestions.results(user, Some(JodaUTC(1)))(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(
          DerivativeQuestionResults(user, question1, List(answer1_1)),
          DerivativeQuestionResults(user, question2, List(answer2_1))
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

        DerivativeQuestions.results(user, Some(JodaUTC(1)), Some(quiz))(derivativeQuestionsTable, derivativeAnswersTable, derivativeQuestion2QuizTable) must beEqualTo(List(DerivativeQuestionResults(user, question1, List(answer1_1))))
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