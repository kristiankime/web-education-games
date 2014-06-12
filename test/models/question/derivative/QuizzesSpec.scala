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
import play.api.db.slick.Config.driver.simple._
import viewsupport.question.derivative._
import models.organization.Courses
import models.organization.CourseTmpTest

@RunWith(classOf[JUnitRunner])
class QuizzesSpec extends Specification {

	"studentResults" should {

		"lists questions with whatever answers they have" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))
				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val student = DBTest.fakeUser(UserTmpTest())
				val answer1 = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id))
				val answer2 = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id))

				val sqr1 = QuestionResults(student, question1, List(answer1, answer2), Some(answer1.creationDate))
				val sr = UserQuizResult(student, quiz, List(sqr1))
				quiz.results(student) must beEqualTo(sr)
			}
		}

		"questions with no answers are incorrect" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))
				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val student = DBTest.fakeUser(UserTmpTest())
				val answer = Answers.createAnswer(AnswerTmpTest(owner = student.id, questionId = question1.id, correct = true))

				val sqr1 = QuestionResults(user, question1, List(answer), Some(answer.creationDate))
				val sqr2 = QuestionResults(user, question2, List(), None)
				val sr = UserQuizResult(student, quiz, List(sqr1, sqr2))
				quiz.results(student) must beEqualTo(sr)
			}
		}
	}

	"questions" should {

		"find all the questions for this quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>

				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))

				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}

		"not find questions for other quizesz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))
				val otherQuiz = Quizzes.create(QuizTmpTest(owner = user.id))

				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				val otherQuestion = Questions.create(QuestionTmpTest(owner = user.id), otherQuiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}

	}

	"access" should {

		"return own if the user owns the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id))

				quiz.access(user, session) must beEqualTo(Own)
			}
		}

		"return non if the user has no relation to the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val quiz = Quizzes.create(QuizTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val user = DBTest.fakeUser(UserTmpTest())
				Courses.create(CourseTmpTest(owner = user.id)) // This course does not contain the quiz so it should have no effect
				
				quiz.access(user, session) must beEqualTo(Non)
			}
		}

		"return edit if the user owns a course that contains the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = user.id))
				val quiz = Quizzes.create(QuizTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id), course.id)

				quiz.access(user, session) must beEqualTo(Edit)
			}
		}

	}

}
