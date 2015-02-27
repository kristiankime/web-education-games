package service.table

import com.artclod.slick.JodaUTC._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import securesocial.core.{IdentityId, _}
import service._
import service.table.SecurityMapper._

// Adapted from http://blog.lunatech.com/2013/07/04/play-securesocial-slick
class LoginsTable(tag: Tag) extends Table[Login](tag, "secure_social_logins") {
  // General
  def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[String]("user_id")
  def providerId = column[String]("provider_id")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def fullName = column[String]("full_name")
  def email = column[Option[String]]("email")
  def avatarUrl = column[Option[String]]("avatar_url")
  def authMethod = column[AuthenticationMethod]("auth_Method")
  // oAuth 1
  def token = column[Option[String]]("token")
  def secret = column[Option[String]]("secret")
  // oAuth 2
  def accessToken = column[Option[String]]("access_token")
  def tokenType = column[Option[String]]("token_type")
  def expiresIn = column[Option[Int]]("expires_in")
  def refreshToken = column[Option[String]]("refresh_token")
  // Password Info
  def hasher = column[Option[String]]("hasher")
  def password = column[Option[String]]("password")
  def salt = column[Option[String]]("salt")
  // Dates
  def creationDate = column[DateTime]("creation_date")
  def updateDate = column[DateTime]("update_date")

  def * = (id, userId, providerId, firstName, lastName, fullName, email, avatarUrl, authMethod, token, secret, accessToken, tokenType, expiresIn, refreshToken, hasher, password, salt, creationDate, updateDate) <>(
    (r: Tuple20[UserId, String, String, String, String, String, Option[String], Option[String], AuthenticationMethod, Option[String], Option[String], Option[String], Option[String], Option[Int], Option[String], Option[String], Option[String], Option[String], DateTime, DateTime]) => Login(r._1, (r._2, r._3), r._4, r._5, r._6, r._7, r._8, r._9, (r._10, r._11), (r._12, r._13, r._14, r._15), (r._16, r._17, r._18), r._19, r._20),
    (u: Login) => Some((u.id, u.identityId.userId, u.identityId.providerId, u.firstName, u.lastName, u.fullName, u.email, u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret), u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn), u.oAuth2Info.flatMap(_.refreshToken), u.passwordInfo.map(_.hasher), u.passwordInfo.map(_.password), u.passwordInfo.flatMap(_.salt), u.creationDate, u.updateDate))
    )
}

object LoginsTable {
  val loginTable = TableQuery[LoginsTable]

  def insert(u: Login)(implicit s: Session) = {
    val userId = (loginTable returning loginTable.map(_.id)) += u
    u.copy(id = userId)
  }

  def findById(id: UserId)(implicit s: Session) = loginTable.where(_.id is id).firstOption

  def findByIdentityId(userId: IdentityId)(implicit s: Session): Option[Login] = {
    (for {
      user <- loginTable if (user.userId is userId.userId) && (user.providerId is userId.providerId)
    } yield user).firstOption
  }

  def findByEmailAndProvider(email: String, providerId: String)(implicit s: Session): Option[Login] = {
    (for {
      user <- loginTable if (user.email is email) && (user.providerId is providerId)
    } yield user).firstOption
  }

  def all(implicit s: Session) = TableQuery[LoginsTable].list

  def save(t: Login)(implicit s: Session) = {
    findByIdentityId(t.identityId) match {
      case None => insert(t)
      case Some(existingUser) => {
        loginTable.where(_.id is existingUser.id).update(existingUser)
        existingUser
      }
    }
  }

}