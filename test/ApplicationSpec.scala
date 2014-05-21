import models.question.derivative.{QuestionTmpTest, Questions, QuizTmpTest, Quizzes}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import com.artclod.securesocial.TestUtils._
import models.DBTest
import models.DBTest.fakeUser
import models.organization.{SectionTmpTest, Sections, Courses, CourseTmpTest}
import play.mvc.SimpleResult
import service.{UserTmpTest => U}
import securesocial.core.PasswordInfo
import service.SlickUserService
import securesocial.core.Identity
import securesocial.core.IdGenerator
import play.api.db.slick.DB
import scala.concurrent.Future
import play.api.db.slick.Config.driver.simple._


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = fakeUser
          val home = route(FakeRequest(GET, "/").withLoggedInUser(user)).get

          status(home) must equalTo(OK)
          contentType(home) must beSome.which(_ == "text/html")
          contentAsString(home) must contain("Welcome to the EdTech server")
      }
    }
  }

  "Courses" should {

    "allow any user to view a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = fakeUser
          val course = Courses.create(CourseTmpTest(owner = fakeUser.id))

          val page = route(FakeRequest(GET, "/courses/" + course.id.v).withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(course.name)
      }
    }

    "allow any user to add a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = fakeUser

          val page = route(FakeRequest(GET, "/courses/add").withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain("Please")
      }
    }
  }


  "Sections" should {

    "allow any user to view a section" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = fakeUser

          val owner = fakeUser
          val course = Courses.create(CourseTmpTest(owner = owner.id))
          val section = Sections.create(SectionTmpTest(owner = owner.id, courseId = course.id))

          val routeStr: String = "/courses/" + course.id.v + "/sections/" + section.id.v
          val page = route(FakeRequest(GET, routeStr).withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(course.name)
      }

      "respond with a BadRequest if the course doesn't match the section" in new WithApplication {
        DB.withSession {
          implicit session: Session =>
            val user = fakeUser

            val owner = fakeUser
            val course = Courses.create(CourseTmpTest(owner = owner.id))
            val section = Sections.create(SectionTmpTest(owner = owner.id, courseId = course.id))

            val routeStr: String = "/courses/" + Long.MaxValue + "/sections/" + section.id.v
            val page = route(FakeRequest(GET, routeStr).withLoggedInUser(user)).get

            status(page) must equalTo(BAD_REQUEST)
        }
      }

    }

  }

  "Quizzes" should {

    "allow any user to view a quiz" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = fakeUser

          val owner = fakeUser
          val quiz = Quizzes.create(QuizTmpTest(owner = owner.id))
          val question = Questions.create(QuestionTmpTest(owner = owner.id), quiz.id)

          val routeStr: String = "/quizzes/" + quiz.id.v + "/questions/" + question.id.v
          val page = route(FakeRequest(GET, routeStr).withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(quiz.name)
      }

    }

  }




}
