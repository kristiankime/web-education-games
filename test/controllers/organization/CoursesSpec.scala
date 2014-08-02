package controllers.organization

import com.artclod.securesocial.TestUtils._
import models.DBTest.newFakeUser
import models.organization.{Courses, Sections, TestCourse, TestSection}
import models.question.derivative.{Questions, Quizzes, TestQuestion, TestQuiz}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._
import service.{UserTest => U}

@RunWith(classOf[JUnitRunner])
class CoursesSpec extends Specification {

  "Courses" should {

    "allow any user to view a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser
          val course = Courses.create(TestCourse(owner = newFakeUser.id))

          val page = route(FakeRequest(GET, "/courses/" + course.id.v).withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(course.name)
      }
    }

    "allow any user to add a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser

          val page = route(FakeRequest(GET, "/courses/add").withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain("Please")
      }
    }

  }

}
