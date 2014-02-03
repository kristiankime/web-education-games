package models.question.derivative

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import mathml.scalar._
import mathml.scalar.apply._
import service.table._
import models.id._
import models.DBTest
import service._
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

//	"Questions" should {
//		"create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
//			val user = DBTest.fakeUser(UserTmpTest())
//			val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", true, DateTime.now)
//			val id = Questions.create(QuestionTmp)
//			val eq = Questions.findQuestion(id)
//
//			eq.get must beEqualTo(Question(id, x + `1`, "x + 1", true, DateTime.now))
//		}

//		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
//			val user = DBTest.fakeUser(UserTmpTest())
//			Questions.create(QuestionTmp(user.id, x + `1`, "x + 2", true))
//			Questions.create(user, x + `2`, "x + 2", true)
//
//			val eqs = Questions.allQuestions.map(_.mathML)
//			eqs must beEqualTo(List(x + `1`, x + `2`))
//		}

//		"return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
//			val eq = Questions.findQuestion(QuestionId(Int.MaxValue))
//
//			eq must beNone
//		}

//		"delete a question when requested" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
//			val user = DBTest.fakeUser(UserTmpTest())
//			val id = Questions.createQuestion(user, x + `2`, "x + 2", true)
//			Questions.deleteQuestion(id)
//			val eq = Questions.findQuestion(id)
//
//			eq must beNone
//		}
//	}

}
