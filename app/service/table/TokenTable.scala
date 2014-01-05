package service.table

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.Token
import com.github.tototoshi.slick.JodaSupport._

object TokenTable extends Table[Token]("token") {
	def uuid = column[String]("uuid", O.PrimaryKey)
	def email = column[String]("email")
	def creationTime = column[DateTime]("creationTime")
	def expirationTime = column[DateTime]("expirationTime")
	def isSignUp = column[Boolean]("isSignUp")
	def * = uuid ~ email ~ creationTime ~ expirationTime ~ isSignUp <> (Token, Token.unapply _)

	def save(token: Token)(implicit s: Session) { TokenTable.insert(token) }

	def findToken(uuid: String)(implicit s: Session): Option[Token] = Query(TokenTable).where(_.uuid is uuid).firstOption

	def deleteToken(uuid: String)(implicit s: Session) { Query(TokenTable).where(_.uuid is uuid).delete }

	def deleteTokens()(implicit s: Session) { Query(TokenTable).delete }

	def deleteExpiredTokens()(implicit s: Session) { Query(TokenTable).where(_.expirationTime <= DateTime.now ).delete }
}