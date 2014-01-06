package models.question

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import mathml.scalar._
import mathml.scalar.apply._
import models.DBTest
import scala.slick.session.Session
import service.table._
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import play.api.test.Helpers.inMemoryDatabase
import play.api.db.slick.DB
import service.UserTmpTest
import models.question.derivative.Questions
import models.question.derivative.Question

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

	"Questions" should {
		"create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val user = DBTest.fakeUser(UserTmpTest())
			val id = Questions.createQuestion(user, x + `1`, "x + 1", true)
			val eq = Questions.findQuestion(id)

			eq.get must beEqualTo(Question(id, x + `1`, "x + 1", true))
		}

		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val user = DBTest.fakeUser(UserTmpTest())
			Questions.createQuestion(user, x + `1`, "x + 2", true)
			Questions.createQuestion(user, x + `2`, "x + 2", true)

			val eqs = Questions.allQuestions.map(_.mathML)
			eqs must beEqualTo(List(x + `1`, x + `2`))
		}

		"return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val eq = Questions.findQuestion(Int.MaxValue)

			eq must beNone
		}

		"delete a question when requested" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val user = DBTest.fakeUser(UserTmpTest())
			val id = Questions.createQuestion(user, x + `2`, "x + 2", true)
			Questions.deleteQuestion(id)
			val eq = Questions.findQuestion(id)

			eq must beNone
		}
	}

}
