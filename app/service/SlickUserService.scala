package service

import play.api.Application
import securesocial.core.{ IdentityId, Identity, UserServicePlugin }
import securesocial.core.providers.Token
import play.api.db.slick.DB
import scala.slick.session.Session
import service.table._
import org.joda.time.DateTime

class SlickUserService(implicit application: Application) extends UserServicePlugin(application) {
	val tokenTable = new TokenTable
	
	// =========== Identity Methods ===========
	def save(identity: Identity) = DB.withSession { implicit s: Session =>
		(new UserTable).save(UserTmp(identity, DateTime.now))
	}

	def find(id: IdentityId) = DB.withSession { implicit s: Session =>
		(new UserTable).findByIdentityId(id)
	}

	def findByEmailAndProvider(email: String, providerId: String) = DB.withSession { implicit s: Session =>
		(new UserTable).findByEmailAndProvider(email, providerId)
	}

	// =========== Token Methods ===========
	def save(token: Token) = DB.withSession { implicit s: Session =>
		tokenTable.save(token)
	}

	def findToken(uuid: String): Option[Token] = DB.withSession { implicit s: Session =>
		tokenTable.findToken(uuid)
	}

	def deleteToken(uuid: String) = DB.withSession { implicit s: Session =>
		tokenTable.deleteToken(uuid)
	}

	def deleteTokens() = DB.withSession { implicit s: Session =>
		tokenTable.deleteTokens()
	}

	def deleteExpiredTokens() = DB.withSession { implicit s: Session =>
		tokenTable.deleteExpiredTokens()
	}
}