package service

import models.support.UserId
import org.joda.time.DateTime
import securesocial.core.{Identity, IdentityId, OAuth1Info, SecuredRequest, _}

case class Login(id: UserId,
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
	updateDate: DateTime) extends Identity {
	def user = this
}

object Login {
	def apply(implicit request: SecuredRequest[_]) = {
		request.user match {
			case user: Login => user
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programmatic error")
		}
	}

  def apply(i: Identity, date: DateTime): Login = Login(null, i.identityId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo, date, date)
}
