package models.question.derivative

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import mathml.scalar._
import mathml.scalar.apply._
import service.table._
import models.id._
import models.DBTest
import models.DBTest.inMemH2
import service._
import org.joda.time.DateTime
import play.api.db.slick.DB
import scala.slick.session.Session
import models.question.derivative.view._

@RunWith(classOf[JUnitRunner])
class QuizzesSpec extends Specification {

	"studentResults" should {

		"lists questions with whatever answers they have" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)
				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val student = DBTest.fakeUser(UserTmpTest())
				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id))
				
				val sqr1 = QuestionResults(question1, List(answer1, answer2))
				val sr = UserQuizResults(student, quiz,  List(sqr1))
				quiz.results(student) must beEqualTo(sr)
			}
		}
	
		"questions with no answers are incorrect" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)
				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val student = DBTest.fakeUser(UserTmpTest())
				val answer = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id, correct = true))
				
				val sqr1 = QuestionResults(question1, List(answer))
				val sqr2 = QuestionResults(question2, List())
				val sr = UserQuizResults(student, quiz, List(sqr1, sqr2))
				quiz.results(student) must beEqualTo(sr)
			}
		}
	}

	"questions" should {

		"find all the questions for this quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>

				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)

				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}

		"not find questions for other quizesz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)
				val otherQuiz = Quizzes.create(QuizTmpTest(owner = user.id), None)

				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val otherQuestion = Questions.create(QuestionTmpTest(owner = user.id), otherQuiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}

	}

}
