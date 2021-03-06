package service

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import securesocial.core.Identity
import securesocial.core.IdentityId
import java.util.UUID
import org.joda.time.{DateTimeZone, DateTime}
import securesocial.core.providers.Token
import service.table._
import com.artclod.slick.JodaUTC

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class SlickUserServiceSpec extends Specification {

	"user management" should {

		"be able to find a user by IdentityId" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val service = new SlickUserService

			val identityId = IdentityId("userId", "providerId")
			val user = service.save(UserTest(identityId = identityId))

			service.find(identityId).get must beEqualTo(user)
		}

		"be able to find a user by email and provider" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val service = new SlickUserService
			val identityId = IdentityId("userId", "providerId")
			val user = service.save(UserTest(identityId = identityId, email = Some("email")))

			service.findByEmailAndProvider("email", "providerId").get must beEqualTo(user)
		}
	}

	"token management" should {
		"be able to find a token by uuid" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val service = new SlickUserService
			val token = TestToken(uuid = "uuid")
			service.save(token)

			service.findToken("uuid").get must beEqualTo(token)
		}

		"be able to delete a token by uuid" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val service = new SlickUserService
			val token = TestToken(uuid = "uuid")

			service.save(token)
			service.deleteToken("uuid")

			service.findToken("uuid") must beEmpty
		}

		"be able to delete all tokens" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val service = new SlickUserService
			val token1 = TestToken(uuid = "uuid1")
			val token2 = TestToken(uuid = "uuid2")

			service.save(token1)
			service.save(token2)
			service.deleteTokens

			service.findToken("uuid1") must beEmpty
			service.findToken("uuid2") must beEmpty
		}
	}
}

object TestToken {
	def apply(uuid: String = UUID.randomUUID.toString,
		email: String = "email",
		creationTime: DateTime = JodaUTC.now,
		expirationTime: DateTime = JodaUTC.now.plusDays(1),
		isSignUp: Boolean = false) = Token(uuid, email, creationTime, expirationTime, isSignUp)
}
