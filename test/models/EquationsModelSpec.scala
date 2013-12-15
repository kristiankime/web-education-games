package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import scala.slick.session.Session

@RunWith(classOf[JUnitRunner])
class EquationsModelSpec extends Specification {

	"EquationsModel" should {

		"create a new equation when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val id = EquationsModel.create("an equation")
				val eq = EquationsModel.read(id)

				eq.get.equation must beEqualTo("an equation")
			}
		}

		"return all the equations that were created when asked" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				EquationsModel.create("equation1")
				EquationsModel.create("equation2")

				val eqs = EquationsModel.all.map(_.equation)
				eqs must beEqualTo(List("equation1", "equation2"))
			}
		}

		"return None when the request equation does not exists" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val eq = EquationsModel.read(Int.MaxValue)

				eq must beNone
			}
		}

		"delete an equation when requested" in new WithApplication {
			DBTest.withSessionAndRollback { implicit s: Session =>
				val id = EquationsModel.create("an equation")
				EquationsModel.delete(id)
				val eq = EquationsModel.read(id)

				eq must beNone
			}
		}

	}

}
