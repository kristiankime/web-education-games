package models.security

import securesocial.core.AuthenticationMethod
import securesocial.core.IdentityId
import securesocial.core.OAuth1Info
import securesocial.core.OAuth2Info
import securesocial.core.PasswordInfo
import securesocial.core.IdentityId

object UserTmpTest {
	def apply(identityId: IdentityId = IdentityId("userId", "providerId"),
		firstName: String = "first",
		lastName: String = "last",
		fullName: String = "first middle last",
		email: Option[String] = None,
		avatarUrl: Option[String] = None,
		authMethod: AuthenticationMethod = AuthenticationMethod("test only"),
		oAuth1Info: Option[OAuth1Info] = None,
		oAuth2Info: Option[OAuth2Info] = None,
		passwordInfo: Option[PasswordInfo] = None) = UserTmp(identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo)
}