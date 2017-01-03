package controllers.quiz.multiplefunction

import controllers.quiz.tangent.TangentQuestionForm
import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._


@RunWith(classOf[JUnitRunner])
class MultipleFunctionQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = MultipleFunctionQuestionForm("desc", "exp", List("op1", "op2"), List("""<cn type="integer">1</cn>""", """<cn type="integer">2</cn>"""), List("1", "2"), 1)
      val question = MultipleFunctionQuestionForm.toQuestion(user, questionForm)
      val options = MultipleFunctionQuestionForm.toOptions(questionForm)
      val questionFormRoundtrip = MultipleFunctionQuestionForm.fromQuestion(question, options.get).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
