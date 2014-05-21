package service

import org.joda.time.DateTime
import play.api.Application
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import securesocial.core.{ IdentityId, Identity, UserServicePlugin }
import securesocial.core.providers.Token
import service.table._

class SlickUserService(implicit application: Application) extends UserServicePlugin(application) {
	// =========== Identity Methods ===========
	def save(identity: Identity) = DB.withSession { implicit s: Session =>
    val user = User(identity, DateTime.now)
		UserTable.save(user)
	}

	def find(id: IdentityId) = DB.withSession { implicit s: Session =>
    UserTable.findByIdentityId(id)
	}

	def findByEmailAndProvider(email: String, providerId: String) = DB.withSession { implicit s: Session =>
    UserTable.findByEmailAndProvider(email, providerId)
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