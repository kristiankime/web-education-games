package service

import securesocial.core._
import play.api.db.slick.Config.driver.simple._
import securesocial.core.Identity
import securesocial.core.IdentityId
import securesocial.core.OAuth1Info

case class User(uid: Long,
	identityId: IdentityId,
	firstName: String,
	lastName: String,
	fullName: String,
	email: Option[String],
	avatarUrl: Option[String],
	authMethod: AuthenticationMethod,
	oAuth1Info: Option[OAuth1Info],
	oAuth2Info: Option[OAuth2Info],
	passwordInfo: Option[PasswordInfo]) extends Identity

// A helper class that is "the same" but missing the uid, to be used when the object has not yet been persisted and thus has no uid
case class UserTmp(identityId: IdentityId,
	firstName: String,
	lastName: String,
	fullName: String,
	email: Option[String],
	avatarUrl: Option[String],
	authMethod: AuthenticationMethod,
	oAuth1Info: Option[OAuth1Info],
	oAuth2Info: Option[OAuth2Info],
	passwordInfo: Option[PasswordInfo]) {
	
	def apply(uid: Long) = User(uid, identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo)
}

object UserTmp {
	def apply(i: Identity): UserTmp = UserTmp(i.identityId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo)
}