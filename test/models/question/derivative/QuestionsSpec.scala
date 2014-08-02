package models.question.derivative

import com.artclod.slick.Joda
import models.organization.{TestSection, Sections, TestCourse, Courses}
import models.organization.assignment.{TestAssignment, Assignments, TestGroup, Groups}
import org.joda.time.{DateTimeZone, DateTime}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import com.artclod.mathml.scalar._
import models.support._
import models.DBTest
import models.DBTest._
import service._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
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
        val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
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
        val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
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
        val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
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
        val qTmp = Question(null, user.id, x + `1`, "x + 1", Joda.now)
        val question = Questions.create(qTmp, quiz.id)
        val eq = Questions(question.id)

        eq must beSome(question)
      }
    }

    "return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = DBTest.newFakeUser(UserTest())
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

  "Questions.create (group version)" should {

    "create a new questions and associate it with a user, quiz and group" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = newFakeUser
        val (course, section, assignment, group) = TestGroup(admin.id)
        val quiz = Quizzes.create(TestQuiz(admin.id), group.id)
        val student = DBTest.newFakeUser(UserTest())

        val question = Questions.create(TestQuestion(student.id), group.id, quiz.id, student.id)

        question.forWho must beSome(student)
        question.group must beSome(group)
        question.quiz must beSome(quiz)
      }
    }


    "throw if a question has already been created for a given user in that quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = newFakeUser
        val (course, section, assignment, group) = TestGroup(admin.id)
        val quiz = Quizzes.create(TestQuiz(admin.id), group.id)
        val student = DBTest.newFakeUser(UserTest())

        val question1 = Questions.create(TestQuestion(student.id), group.id, quiz.id, student.id)
        Questions.create(TestQuestion(student.id), group.id, quiz.id, student.id) must throwAn[IllegalStateException]
      }
    }

    "not throw if a question has already been created for a given user in a different quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = newFakeUser
        val (course, section, assignment, group) = TestGroup(admin.id)
        val quiz1 = Quizzes.create(TestQuiz(admin.id), group.id)
        val quiz2 = Quizzes.create(TestQuiz(admin.id), group.id)
        val student = DBTest.newFakeUser(UserTest())

        val question1 = Questions.create(TestQuestion(student.id), group.id, quiz1.id, student.id)

        val question2 = Questions.create(TestQuestion(student.id), group.id, quiz2.id, student.id)
        question2.forWho must beSome(student)
        question2.group must beSome(group)
        question2.quiz must beSome(quiz2)
      }
    }

  }

}