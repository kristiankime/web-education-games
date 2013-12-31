package service

import play.api.Application
import securesocial.core.{ IdentityId, Identity, UserServicePlugin }
import securesocial.core.providers.Token
import models.UsersTable
import play.api.db.slick.DB
import scala.slick.session.Session
import models.TokenTable

class SlickUserService(implicit application: Application) extends UserServicePlugin(application) {

	// =========== Identity Methods ===========
	def find(id: IdentityId) = DB.withSession { implicit s: Session =>
		UsersTable.findByIdentityId(id)
	}

	def save(user: Identity) = DB.withSession { implicit s: Session =>
		UsersTable.save(user)
	}

	def findByEmailAndProvider(email: String, providerId: String) = DB.withSession { implicit s: Session =>
		UsersTable.findByEmailAndProvider(email, providerId)
	}

	// =========== Token Methods ===========
	def save(token: Token) = DB.withSession { implicit s: Session =>
		TokenTable.save(token)
	}

	def findToken(uuid: String): Option[Token] = DB.withSession { implicit s: Session =>
		TokenTable.findToken(uuid)
	}

	def deleteToken(uuid: String) = DB.withSession { implicit s: Session =>
		TokenTable.deleteToken(uuid)
	}

	def deleteTokens() = DB.withSession { implicit s: Session =>
		TokenTable.deleteTokens()
	}

	def deleteExpiredTokens() = DB.withSession { implicit s: Session =>
		TokenTable.deleteExpiredTokens()
	}
}