package service

import play.api.Application
import securesocial.core.{ IdentityId, Identity, UserServicePlugin }
import securesocial.core.providers.Token
import models.UsersTable
import play.api.db.slick.DB
import scala.slick.session.Session

class SlickUserService(implicit application: Application) extends UserServicePlugin(application) {

	def find(id: IdentityId) = DB.withSession { implicit s: Session =>
		UsersTable.findByIdentityId(id)
	}

	def save(user: Identity) = DB.withSession { implicit s: Session =>
		UsersTable.save(user)
	}

	// TODO implement these
	def findByEmailAndProvider(email: String, providerId: String) = None
	def save(token: Token) {}
	def findToken(token: String) = None
	def deleteToken(uuid: String) {}
	def deleteExpiredTokens() {}
}