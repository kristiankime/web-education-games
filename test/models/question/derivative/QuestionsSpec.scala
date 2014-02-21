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
import models.DBTest.inMemH2
import service._
import org.joda.time.DateTime
import play.api.db.slick.DB
import scala.slick.session.Session

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

	"Questions" should {
		"create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val qzId = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)
				val qTmp = QuestionTmp(user.id, x + `1`, "x + 1", DateTime.now)
				val question = Questions.create(qTmp, qzId)
				val eq = Questions.find(question.id)

				eq.get must beEqualTo(question)
			}
		}

		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.fakeUser(UserTmpTest())
				val qzId = Quizzes.create(QuizTmp(user.id, "test", DateTime.now), None)

				Questions.create(QuestionTmp(user.id, x + `1`, "x + 2", DateTime.now), qzId)
				Questions.create(QuestionTmp(user.id, x + `2`, "x + 2", DateTime.now), qzId)

				val eqs = Questions.allQuestions.map(_.mathML)
				eqs must beEqualTo(List(x + `1`, x + `2`))
			}
		}

		"return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val eq = Questions.find(QuestionId(Int.MaxValue))

				eq must beNone
			}
		}
	}

}
