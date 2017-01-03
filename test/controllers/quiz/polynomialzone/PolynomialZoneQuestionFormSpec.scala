package controllers.quiz.polynomialzone

import controllers.quiz.tangent.TangentQuestionForm
import models.quiz.question.support.{SecondDerivativeConcaveUp, PolynomialZoneType}
import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._


@RunWith(classOf[JUnitRunner])
class PolynomialZoneQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = PolynomialZoneQuestionForm("1,2,3", 1d, SecondDerivativeConcaveUp.order)
      val question = PolynomialZoneQuestionForm.toQuestion(user, questionForm)
      val questionFormRoundtrip = PolynomialZoneQuestionForm.fromQuestion(question).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
