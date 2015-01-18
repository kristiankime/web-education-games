package models.tournament

import models.{user, DBTest}
import models.DBTest.inMemH2
import models.organization._
import models.quiz.question.TestDerivativeQuestion
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.{FakeApplication, _}
import service.{UserTest, _}

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class TournamentsSpec extends Specification {

	"studentScoresRankingFor" should {
		
		"return nothing if no questions have been answered" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val user = DBTest.newFakeUser(UserTest())

				val rankings = Tournaments.studentScoresRankingFor(user.id, 3)

				rankings.ranks must haveSize(0)
				rankings.user must beEmpty
			}
		}

		"return average score of question difficulty" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val questioner = DBTest.newFakeUser(UserTest())
				val answerer = DBTest.newFakeUser(UserTest(fullName="answerer"))
				TestDerivativeQuestion.create(questioner.id, difficulty=15d, answered=Some(answerer.id))
				TestDerivativeQuestion.create(questioner.id, difficulty=5d, answered=Some(answerer.id))

				val rankings = Tournaments.studentScoresRankingFor(answerer.id, 3)

				rankings.ranks must beEqualTo(List(Rank(answerer.id, "answerer", 10d, 1)))
				rankings.user must beEmpty
			}
		}

		"return scores of all players" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val questioner = DBTest.newFakeUser(UserTest())
				val answerer1 = DBTest.newFakeUser(UserTest(fullName = "answerer1"))
				val answerer2 = DBTest.newFakeUser(UserTest(fullName = "answerer2"))
				val answerer3 = DBTest.newFakeUser(UserTest(fullName = "answerer3"))
				TestDerivativeQuestion.create(questioner.id, difficulty = 3d, answered = Some(answerer1.id))
				TestDerivativeQuestion.create(questioner.id, difficulty = 2d, answered = Some(answerer2.id))
				TestDerivativeQuestion.create(questioner.id, difficulty = 1d, answered = Some(answerer3.id))

				val rankings = Tournaments.studentScoresRankingFor(answerer1.id, 3)

				rankings.ranks must beEqualTo(List(
					Rank(answerer1.id, "answerer1", 3d, 1),
					Rank(answerer2.id, "answerer2", 2d, 2),
					Rank(answerer3.id, "answerer3", 1d, 3))
				)
				rankings.user must beEmpty
			}
		}

		"return scores of all players (up to requested limit)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val questioner = DBTest.newFakeUser(UserTest())
				val answerer1 = DBTest.newFakeUser(UserTest(fullName = "answerer1"))
				val answerer2 = DBTest.newFakeUser(UserTest(fullName = "answerer2"))
				val answerer3 = DBTest.newFakeUser(UserTest(fullName = "answerer3"))
				TestDerivativeQuestion.create(questioner.id, difficulty = 3d, answered = Some(answerer1.id))
				TestDerivativeQuestion.create(questioner.id, difficulty = 2d, answered = Some(answerer2.id))
				TestDerivativeQuestion.create(questioner.id, difficulty = 1d, answered = Some(answerer3.id))

				val rankings = Tournaments.studentScoresRankingFor(answerer1.id, 2)

				rankings.ranks must beEqualTo(List(
					Rank(answerer1.id, "answerer1", 3d, 1),
					Rank(answerer2.id, "answerer2", 2d, 2))
				)
				rankings.user must beEmpty
			}
		}

	}

}
