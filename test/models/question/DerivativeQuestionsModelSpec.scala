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
import models.security.UserTable
import models.security.UserTmp
import models.security.UserTmpTest
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import play.api.test.Helpers.inMemoryDatabase
import play.api.db.slick.DB

@RunWith(classOf[JUnitRunner])
class QuestionsSpec extends Specification {

	"Questions" should {

		"create a new questions when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			//			DBTest.withSessionAndRollback { implicit s: Session =>
			DB.withSession { implicit session: Session =>
				val user = UserTable.save(UserTmpTest())

				val id = Questions.create(user, x + `1`, "x + 1", true)
				val eq = Questions.read(id)

				eq.get must beEqualTo(Question(id, x + `1`, "x + 1", true))
			}
		}

		"return all the questions that were created when asked" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			//			DBTest.withSessionAndRollback { implicit s: Session =>
			DB.withSession { implicit session: Session =>
				val user = UserTable.save(UserTmpTest())

				Questions.create(user, x + `1`, "x + 2", true)
				Questions.create(user, x + `2`, "x + 2", true)

				val eqs = Questions.all.map(_.mathML)
				eqs must beEqualTo(List(x + `1`, x + `2`))
			}
		}

		"return None when the request question does not exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			//			DBTest.withSessionAndRollback { implicit s: Session =>
			DB.withSession { implicit session: Session =>
				val eq = Questions.read(Int.MaxValue)

				eq must beNone
			}
		}

		"delete a question when requested" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			//			DBTest.withSessionAndRollback { implicit s: Session =>
			DB.withSession { implicit session: Session =>
				val user = UserTable.save(UserTmpTest())

				val id = Questions.create(user, x + `2`, "x + 2", true)
				Questions.delete(id)
				val eq = Questions.read(id)

				eq must beNone
			}
		}

	}

}
