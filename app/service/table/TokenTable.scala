package service.table

import com.artclod.slick.Joda
import com.artclod.slick.Joda._
import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.Token
import org.joda.time.DateTime

class TokenTable(tag: Tag) extends Table[Token](tag, "token") {
  def uuid = column[String]("uuid", O.PrimaryKey)
  def email = column[String]("email")
  def creationTime = column[DateTime]("creationTime")
  def expirationTime = column[DateTime]("expirationTime")
  def isSignUp = column[Boolean]("isSignUp")

  def * = (uuid, email, creationTime, expirationTime, isSignUp) <> (Token.tupled, Token.unapply _)
}

object TokenTable {
  val tokenTable = TableQuery[TokenTable]

  def save(token: Token)(implicit s: Session) = tokenTable.insert(token)

  def findToken(uuid: String)(implicit s: Session): Option[Token] = tokenTable.where(_.uuid is uuid).firstOption

  def deleteToken(uuid: String)(implicit s: Session) = tokenTable.where(_.uuid is uuid).delete

  def deleteTokens()(implicit s: Session) = tokenTable.delete

  def deleteExpiredTokens()(implicit s: Session) = tokenTable.where(_.expirationTime <= Joda.now).delete
}