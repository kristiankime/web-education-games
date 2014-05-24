package service

import _root_.java.util.concurrent.atomic.AtomicLong
import securesocial.core._
import org.joda.time.{DateTimeZone, DateTime}
import models.support.UserId

object UserTmpTest {
  private val id = new AtomicLong(-1000L)

	def apply(identityId: IdentityId = IdentityId("userId", "providerId"),
		firstName: String = "first",
		lastName: String = "last",
		fullName: String = "first middle last",
		email: Option[String] = None,
		avatarUrl: Option[String] = None,
		authMethod: AuthenticationMethod = AuthenticationMethod("test only"),
		oAuth1Info: Option[OAuth1Info] = None,
		oAuth2Info: Option[OAuth2Info] = None,
		passwordInfo: Option[PasswordInfo] = None,
		date: DateTime = new DateTime(0L, DateTimeZone.UTC)) =

    new User(UserId(id.getAndDecrement),
      identityId,
      firstName,
      lastName,
      fullName,
      email,
      avatarUrl,
      authMethod,
      oAuth1Info,
      oAuth2Info,
      passwordInfo,
      date,
      date) with Identity
}