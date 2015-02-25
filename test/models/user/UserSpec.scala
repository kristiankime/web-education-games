package models.user

import com.artclod.util.TryUtil._
import models.DBTest._
import models.quiz.question.TestDerivativeQuestion
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.db.slick._
import play.api.test.{FakeApplication, WithApplication}

@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification {

  "studentSkillLevel" should {
    "return 1 if the student hasn't answered any qustions" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = newFakeUser

        user.studentSkillLevel must beEqualTo(1d)
      }
    }

    "return average of the difficulty of questions answered correctly" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = newFakeUser
        TestDerivativeQuestion.create(user.id, difficulty = 15d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 5d, answered = Some(user.id))

        user.studentSkillLevel must beEqualTo(10d)
      }
    }

    "return average of the top 5 most difficul of questions if more then 5 have been answered correctly" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val user = newFakeUser
        TestDerivativeQuestion.create(user.id, difficulty = 10d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 10d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 10d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 10d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 10d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 5d, answered = Some(user.id))
        TestDerivativeQuestion.create(user.id, difficulty = 5d, answered = Some(user.id))

        user.studentSkillLevel must beEqualTo(10d)
      }
    }
  }

}
