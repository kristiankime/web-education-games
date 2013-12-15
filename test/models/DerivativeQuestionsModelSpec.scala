package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import scala.slick.session.Session

import mathml.scalar._
import mathml.scalar.apply._

@RunWith(classOf[JUnitRunner])
class DerivativeQuestionsModelSpec extends Specification {

	"DerivativeQuestionsModel" should {

		"create a new questions when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>

				val id = DerivativeQuestionsModel.create(x + `1`, "x + 1", true)
				val eq = DerivativeQuestionsModel.read(id)

				eq.get must beEqualTo(DerivativeQuestion(id, x + `1`, "x + 1", true))
			}
		}

		"return all the questions that were created when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				DerivativeQuestionsModel.create(x + `1`, "x + 2", true)
				DerivativeQuestionsModel.create(x + `2`, "x + 2", true)

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
				val id = DerivativeQuestionsModel.create(x + `2`, "x + 2", true)
				EquationsModel.delete(id)
				val eq = EquationsModel.read(id)

				eq must beNone
			}
		}

	}

}
