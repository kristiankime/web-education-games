package controllers.quiz.graphmatch

import models.support.UserId
import models.user.User
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._


@RunWith(classOf[JUnitRunner])
class GraphMatchQuestionFormSpec extends Specification {

  val user = User(UserId(0), name = "name", lastAccess = new DateTime(0, 1, 1, 0, 0))

  "fromQuestion" should {

    "return same object on roundtrip" in {
      val questionForm = GraphMatchQuestionForm("""<cn type="integer">1</cn>""", "1", """<cn type="integer">2</cn>""", "2", """<cn type="integer">3</cn>""", "3", 1)
      val question = GraphMatchQuestionForm.toQuestion(user, questionForm)
      val questionFormRoundtrip = GraphMatchQuestionForm.fromQuestion(question).get

      questionForm must beEqualTo(questionFormRoundtrip)
    }

  }

}
