package service.table

import com.artclod.slick.JodaUTC
import com.artclod.slick.JodaUTC._
import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.Token
import org.joda.time.DateTime

class TokensTable(tag: Tag) extends Table[Token](tag, "secure_social_tokens") {
  def uuid = column[String]("uuid", O.PrimaryKey)
  def email = column[String]("email")
  def creationTime = column[DateTime]("creation_time")
  def expirationTime = column[DateTime]("expiration_time")
  def isSignUp = column[Boolean]("is_sign_up")

  def * = (uuid, email, creationTime, expirationTime, isSignUp) <> (Token.tupled, Token.unapply _)
}

object TokensTable {
  val tokenTable = TableQuery[TokensTable]

  def save(token: Token)(implicit s: Session) = tokenTable.insert(token)

  def findToken(uuid: String)(implicit s: Session): Option[Token] = tokenTable.where(_.uuid === uuid).firstOption

  def deleteToken(uuid: String)(implicit s: Session) = tokenTable.where(_.uuid === uuid).delete

  def deleteTokens()(implicit s: Session) = tokenTable.delete

  def deleteExpiredTokens()(implicit s: Session) = tokenTable.where(_.expirationTime <= JodaUTC.now).delete
}