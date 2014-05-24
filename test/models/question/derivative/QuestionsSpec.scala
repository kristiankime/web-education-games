package models.question.derivative

import com.artclod.slick.Joda
import org.joda.time.{DateTimeZone, DateTime}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import com.artclod.mathml.scalar._
import models.support._
import models.DBTest
import models.DBTest.inMemH2
import service._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import viewsupport.question.derivative.QuestionResults

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

	"results" should {
		"find all the answers by the user and record false if all of the answers were wrong" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))
				val question = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = false))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = false))

				question.results(user) must beEqualTo(QuestionResults(question, List(answer1, answer2)))
			}
		}
		
		"find all the answers by the user and record true if any of the answers were correct" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))
				val question = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = false))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = true))

				question.results(user) must beEqualTo(QuestionResults(question, List(answer1, answer2)))
			}
		}
	}

	"answers" should {
		"find all the answers by the user" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
				val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
				val question = Questions.create(qTmp, quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))

				question.answers(user) must beEqualTo(List(answer1, answer2))
			}
		}
		
		"not find answers by other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
				val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
				val question = Questions.create(qTmp, quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))

				val otherAnswer = Answers.createAnswer(AnswerTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id, questionId = question.id))
				
				question.answers(user) must beEqualTo(List(answer1, answer2))
			}
		}
		
		"not find answers to other questions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
				val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
				val question = Questions.create(qTmp, quiz.id)
				val otherQuestion = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))

				val otherAnswer = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = otherQuestion.id))
				
				question.answers(user) must beEqualTo(List(answer1, answer2))
			}
		}
	}

	"Questions" should {
		"create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))
				val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
				val question = Questions.create(qTmp, quiz.id)
				val eq = Questions(question.id)

				eq.get must beEqualTo(question)
			}
		}

		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(Quiz(null, user.id, "test", new DateTime(0L, DateTimeZone.UTC), new DateTime(0L, DateTimeZone.UTC)))

				Questions.create(Question(null, user.id, x + `1`, "x + 2", Joda.now), quiz.id)
				Questions.create(Question(null, user.id, x + `2`, "x + 2", Joda.now), quiz.id)

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

}
