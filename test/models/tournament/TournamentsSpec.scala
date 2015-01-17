package models.tournament

import models.DBTest
import models.DBTest.inMemH2
import models.organization._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.{FakeApplication, _}
import service.{UserTest, _}
import models.tournament.Rank
import models.tournament.Rankings

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class TournamentsSpec extends Specification {

	"studentScoresRankingFor" should {
		
		"return nothing if no questions have been answered" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
//        val organization = Organizations.create(TestOrganization())
//        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val user = DBTest.newFakeUser(UserTest())
//				course.grantAccess(Edit)(user, session)
//				course.access(user, session) must beEqualTo(Edit)
				val rankings = Tournaments.studentScoresRankingFor(user.id, 3)
				rankings.ranks must haveSize(0)
				rankings.user must beEmpty
			}
		}

		"return average score of question difficulty" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				//        val organization = Organizations.create(TestOrganization())
				//        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val user = DBTest.newFakeUser(UserTest())

				//				course.grantAccess(Edit)(user, session)
				//				course.access(user, session) must beEqualTo(Edit)
				val rankings = Tournaments.studentScoresRankingFor(user.id, 3)
				rankings.ranks must haveSize(0)
				rankings.user must beEmpty
			}
		}

	}

}
