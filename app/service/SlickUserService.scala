package service

import com.artclod.slick.JodaUTC
import play.api.Application
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import securesocial.core.{ IdentityId, Identity, UserServicePlugin }
import securesocial.core.providers.Token
import service.table._

class SlickUserService(implicit application: Application) extends UserServicePlugin(application) {
	// =========== Identity Methods ===========
	def save(identity: Identity) = DB.withSession { implicit s: Session =>
    val user = User(identity, JodaUTC.now)
		UsersTable.save(user)
	}

	def find(id: IdentityId) = DB.withSession { implicit s: Session =>
    UsersTable.findByIdentityId(id)
	}

	def findByEmailAndProvider(email: String, providerId: String) = DB.withSession { implicit s: Session =>
    UsersTable.findByEmailAndProvider(email, providerId)
	}

	// =========== Token Methods ===========
	def save(token: Token) = DB.withSession { implicit s: Session =>
		TokensTable.save(token)
	}

	def findToken(uuid: String): Option[Token] = DB.withSession { implicit s: Session =>
    TokensTable.findToken(uuid)
	}

	def deleteToken(uuid: String) = DB.withSession { implicit s: Session =>
    TokensTable.deleteToken(uuid)
	}

	def deleteTokens() = DB.withSession { implicit s: Session =>
    TokensTable.deleteTokens()
	}

	def deleteExpiredTokens() = DB.withSession { implicit s: Session =>
    TokensTable.deleteExpiredTokens()
	}
}