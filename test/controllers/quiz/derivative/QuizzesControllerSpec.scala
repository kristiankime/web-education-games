package controllers.quiz.derivative

import com.artclod.securesocial.TestUtils._
import models.DBTest.newFakeUser
import models.organization._
import models.quiz.{TestQuiz, Quizzes}
import models.quiz.question.{TestDerivativeQuestion, DerivativeQuestions}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._
import service.{UserTest => U, Logins}


@RunWith(classOf[JUnitRunner])
class QuizzesControllerSpec extends Specification {

  "Quizzes" should {

    "allow any user to view a quiz" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser

          val owner = newFakeUser
          val organization = Organizations.create(TestOrganization())
          val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
          val quiz = Quizzes.create(TestQuiz(owner = owner.id), course.id)
          val question = DerivativeQuestions.create(TestDerivativeQuestion(owner = owner.id), quiz.id)

          val routeStr: String = "/orgs/" + organization.id.v + "/courses/" + course.id.v + "/quizzes/" + quiz.id.v + "/questions/" + question.id.v

          val page = route(FakeRequest(GET, routeStr).withLoggedInUser(Logins(user.id).get)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(quiz.name)
      }

    }

  }

}
