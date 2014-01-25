package models.id


import play.api.db.slick.Config.driver.simple._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import scala.slick.session.Session
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import models.EquationsModel
import models.DBTest
import service.UserTmpTest
import models.organization._
import models.organization.table._
import service._
import service.table._

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class QueriesSpec extends Specification {

	"access" should {

		"be Non for when a user has no connection to a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {

			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())

			Courses.create(CoursesTmpTest(owner = owner.id))
			val course = Courses.list(accessor)(0)
			
			course.access must beEqualTo(Non)
		}

	}

}
