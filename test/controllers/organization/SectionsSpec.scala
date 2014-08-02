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
class SectionsSpec extends Specification {

  "Sections" should {

    "allow any user to view a section" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser

          val owner = newFakeUser
          val course = Courses.create(TestCourse(owner = owner.id))
          val section = Sections.create(TestSection(owner = owner.id, courseId = course.id))

          val routeStr: String = "/courses/" + course.id.v + "/sections/" + section.id.v
          val page = route(FakeRequest(GET, routeStr).withLoggedInUser(user)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(course.name)
      }

      "respond with a BadRequest if the course doesn't match the section" in new WithApplication {
        DB.withSession {
          implicit session: Session =>
            val user = newFakeUser

            val owner = newFakeUser
            val course = Courses.create(TestCourse(owner = owner.id))
            val section = Sections.create(TestSection(owner = owner.id, courseId = course.id))

            val routeStr: String = "/courses/" + Long.MaxValue + "/sections/" + section.id.v
            val page = route(FakeRequest(GET, routeStr).withLoggedInUser(user)).get

            status(page) must equalTo(BAD_REQUEST)
        }
      }

    }

  }

}
