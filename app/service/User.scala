package service

import securesocial.core._
import securesocial.core.Identity
import securesocial.core.IdentityId
import securesocial.core.OAuth1Info
import securesocial.core.SecuredRequest
import models.support.UserId
import org.joda.time.DateTime

trait HasUserId {
	def id: UserId
	def user: User
}

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
	updateDate: DateTime) extends Identity with HasUserId {
	def user = this
}

object User {
	def apply(implicit request: SecuredRequest[_]) = {
		request.user match {
			case user: User => user
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programmatic error")
		}
	}

  def apply(i: Identity, date: DateTime): User = User(null, i.identityId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo, date, date)
}
