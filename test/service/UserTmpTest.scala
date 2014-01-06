package service

import securesocial.core._

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
		passwordInfo: Option[PasswordInfo] = None) = new UserTmp(identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo) with Identity
}