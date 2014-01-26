package service

import securesocial.core._
import play.api.db.slick.Config.driver.simple._
import securesocial.core.Identity
import securesocial.core.IdentityId
import securesocial.core.OAuth1Info
import securesocial.core.SecuredRequest
import play.api.mvc.AnyContent
import play.api.mvc.Result
import models.id.UserId
import org.joda.time.DateTime

case class User(id: UserId,
	identityId: IdentityId,
	firstName: String,
	lastName: String,
	fullName: String,
	email: Option[String],
	avatarUrl: Option[String],
	authMethod: AuthenticationMethod,
	oAuth1Info: Option[OAuth1Info],
	oAuth2Info: Option[OAuth2Info],
	passwordInfo: Option[PasswordInfo],
	creationDate: DateTime,
	updateDate: DateTime) extends Identity

object User {
	def apply(implicit request: SecuredRequest[_]) = {
		request.user match {
			case user: User => user
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programatic error")
		}
	}
}

// A helper class that is "the same" but missing the id, to be used when the object has not yet been persisted and thus has no id
case class UserTmp(identityId: IdentityId,
	firstName: String,
	lastName: String,
	fullName: String,
	email: Option[String],
	avatarUrl: Option[String],
	authMethod: AuthenticationMethod,
	oAuth1Info: Option[OAuth1Info],
	oAuth2Info: Option[OAuth2Info],
	passwordInfo: Option[PasswordInfo],
	date: DateTime) {

	def apply(id: UserId) = User(id, identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo, date, date)

	def apply(user: User) = User(user.id, identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo, user.creationDate, date)
}

object UserTmp {
	def apply(i: Identity, date: DateTime): UserTmp = UserTmp(i.identityId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo, date)
}