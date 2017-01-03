package controllers.quiz.tangent

import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import models.quiz.question.support.FuncFirstSecond
import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._


@RunWith(classOf[JUnitRunner])
class TangentQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = TangentQuestionForm("""<cn type="integer">1</cn>""", "1","""<cn type="integer">2</cn>""", "2")
      val question = TangentQuestionForm.toQuestion(user, questionForm)
      val questionFormRoundtrip = TangentQuestionForm.fromQuestion(question).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
