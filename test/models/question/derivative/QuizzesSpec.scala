package models.question.derivative

import com.artclod.slick.Joda
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import models.DBTest._
import service._
import viewsupport.question.derivative._
import models.organization.Courses
import models.organization.TestCourse

@RunWith(classOf[JUnitRunner])
class QuizzesSpec extends Specification {

	"studentResults" should {
		"lists questions with whatever answers they have" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = newFakeUser(UserTmpTest())
				val quiz = Quizzes.create(TestQuiz(owner = user.id))
				val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
				val student = newFakeUser(UserTmpTest())
        val startTime = fakeStartWorkingOn(student, question1.id, Joda.zero)
				val answer1 = Answers.createAnswer(TestAnswer(owner = student.id, questionId = question1.id))
				val answer2 = Answers.createAnswer(TestAnswer(owner = student.id, questionId = question1.id))

				val sqr1 = QuestionResults(student, question1, List(answer1, answer2), Some(startTime))
				val sr = StudentQuizResults(student, quiz, List(sqr1))
				quiz.results(student) must beEqualTo(sr)
			}
		}

		"questions with no answers are incorrect" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = newFakeUser(UserTmpTest())
				val quiz = Quizzes.create(TestQuiz(owner = user.id))
				val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
				val question2 = Questions.create(TestQuestion(owner = user.id), quiz.id)
				val student = newFakeUser(UserTmpTest())
        val startTime = fakeStartWorkingOn(student, question1.id, Joda.zero)
        val answer = Answers.createAnswer(TestAnswer(owner = student.id, questionId = question1.id, correct = true))

				val sqr1 = QuestionResults(student, question1, List(answer), Some(startTime))
				val sqr2 = QuestionResults(student, question2, List(), None)
				val sr = StudentQuizResults(student, quiz, List(sqr1, sqr2))
				quiz.results(student) must beEqualTo(sr)
			}
		}
	}

	"questions" should {
		"find all the questions for this quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>

				val user = newFakeUser(UserTmpTest())
				val quiz = Quizzes.create(TestQuiz(owner = user.id))

				val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
				val question2 = Questions.create(TestQuestion(owner = user.id), quiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}

		"not find questions for other quizesz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = newFakeUser(UserTmpTest())
				val quiz = Quizzes.create(TestQuiz(owner = user.id))
				val otherQuiz = Quizzes.create(TestQuiz(owner = user.id))

				val question1 = Questions.create(TestQuestion(owner = user.id), quiz.id)
				val question2 = Questions.create(TestQuestion(owner = user.id), quiz.id)

				val otherQuestion = Questions.create(TestQuestion(owner = user.id), otherQuiz.id)

				quiz.questions must beEqualTo(List(question1, question2))
			}
		}
	}

	"access" should {
		"return own if the user owns the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = newFakeUser(UserTmpTest())
				val quiz = Quizzes.create(TestQuiz(owner = user.id))

				quiz.access(user, session) must beEqualTo(Own)
			}
		}

		"return non if the user has no relation to the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val quiz = Quizzes.create(TestQuiz(owner = newFakeUser(UserTmpTest()).id))
				val user = newFakeUser(UserTmpTest())
				Courses.create(TestCourse(owner = user.id)) // This course does not contain the quiz so it should have no effect
				
				quiz.access(user, session) must beEqualTo(Non)
			}
		}

		"return edit if the user owns a course that contains the quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = user.id))
				val quiz = Quizzes.create(TestQuiz(owner = newFakeUser(UserTmpTest()).id), course.id)

				quiz.access(user, session) must beEqualTo(Edit)
			}
		}
	}

  "quiz.course" should {

    "return a course if the quiz is associated with a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = newFakeUser(UserTmpTest())
        val course = Courses.create(TestCourse(owner = user.id))
        val quiz = Quizzes.create(TestQuiz(owner = user.id), course.id)

        quiz.course.get must beEqualTo(course)
      }
    }

    "return none if the quiz is not with a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = newFakeUser(UserTmpTest())
        val quiz = Quizzes.create(TestQuiz(owner = user.id))

        quiz.course must beNone
      }
    }

  }

}
