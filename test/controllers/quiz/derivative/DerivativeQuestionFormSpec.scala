package controllers.quiz.derivative

import com.artclod.securesocial.TestUtils._
import models.DBTest.newFakeUser
import models.organization._
import models.quiz.question.{DerivativeQuestion, DerivativeQuestions, TestDerivativeQuestion}
import models.quiz.{Quizzes, TestQuiz}
import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._
import service.{Logins, UserTest => U}
import views.html.tag.name


@RunWith(classOf[JUnitRunner])
class DerivativeQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = DerivativeQuestionForm("""<cn type="integer">1</cn>""", "1")
      val question = DerivativeQuestionForm.toQuestion(user, questionForm)
      val questionFormRoundtrip = DerivativeQuestionForm.fromQuestion(question).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
