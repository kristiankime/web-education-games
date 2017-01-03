package controllers.quiz.multiplechoice

import controllers.quiz.multiplechoice.MultipleChoiceQuestionForm
import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._


@RunWith(classOf[JUnitRunner])
class MultipleChoiceQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = MultipleChoiceQuestionForm("desc", "exp", 1, List("op1", "op2"), 1)
      val question = MultipleChoiceQuestionForm.toQuestion(user, questionForm)
      val options = MultipleChoiceQuestionForm.toOptions(questionForm)
      val questionFormRoundtrip = MultipleChoiceQuestionForm.fromQuestion(question, options).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
