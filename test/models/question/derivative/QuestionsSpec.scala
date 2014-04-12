package models.question.derivative

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.apply._
import service.table._
import models.support._
import models.DBTest
import models.DBTest.inMemH2
import service._
import org.joda.time.DateTime
import play.api.db.slick.DB
import scala.slick.session.Session
import viewsupport.question.derivative.QuestionDetails
import viewsupport.question.derivative.QuestionResults

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

	"results" should {
		"find all the answers by the user and record false if all of the answers were wrong" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)
				val question = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = false))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id, correct = false))

				question.results(user) must beEqualTo(QuestionResults(question, List(answer1, answer2)))
			}
		}
		
		"find all the answers by the user and record true if any of the answers were correct" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)
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
				val quiz = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)
				val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", DateTime.now)
				val question = Questions.create(qTmp, quiz.id)

				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = user.id, questionId = question.id))

				question.answers(user) must beEqualTo(List(answer1, answer2))
			}
		}
		
		"not find answers by other users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)
				val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", DateTime.now)
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
				val quiz = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)
				val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", DateTime.now)
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
				val quiz = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)
				val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", DateTime.now)
				val question = Questions.create(qTmp, quiz.id)
				val eq = Questions.find(question.id)

				eq.get must beEqualTo(question)
			}
		}

		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)

				Questions.create(QuestionTmp(user.id, x + `1`, "x + 2", DateTime.now), quiz.id)
				Questions.create(QuestionTmp(user.id, x + `2`, "x + 2", DateTime.now), quiz.id)

				val eqs = Questions.allQuestions.map(_.mathML)
				eqs must beEqualTo(List(x + `1`, x + `2`))
			}
		}

		"return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val eq = Questions.find(QuestionId(Int.MaxValue))

				eq must beNone
			}
		}
	}

}
