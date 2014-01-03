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

@RunWith(classOf[JUnitRunner])
class DerivativeQuestionsModelSpec extends Specification {

	"DerivativeQuestionsModel" should {

		"create a new questions when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val user = UserTable.save(UserTmpTest())

				val id = DerivativeQuestionsModel.create(user, x + `1`, "x + 1", true)
				val eq = DerivativeQuestionsModel.read(id)

				eq.get must beEqualTo(DerivativeQuestion(id, x + `1`, "x + 1", true))
			}
		}

		"return all the questions that were created when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val user = UserTable.save(UserTmpTest())

				DerivativeQuestionsModel.create(user, x + `1`, "x + 2", true)
				DerivativeQuestionsModel.create(user, x + `2`, "x + 2", true)

				val eqs = DerivativeQuestionsModel.all.map(_.mathML)
				eqs must beEqualTo(List(x + `1`, x + `2`))
			}
		}

		"return None when the request question does not exists" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val eq = DerivativeQuestionsModel.read(Int.MaxValue)

				eq must beNone
			}
		}

		"delete a question when requested" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val user = UserTable.save(UserTmpTest())

				val id = DerivativeQuestionsModel.create(user, x + `2`, "x + 2", true)
				DerivativeQuestionsModel.delete(id)
				val eq = DerivativeQuestionsModel.read(id)

				eq must beNone
			}
		}

	}

}
