package service.table

import securesocial.core._
import play.api.db.slick.Config.driver.simple._
import securesocial.core.IdentityId
import service.table.SecurityMapper._
import service._
import models.id._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._

// Adapted from http://blog.lunatech.com/2013/07/04/play-securesocial-slick
class UserTable extends Table[User]("user") {
	// General
	def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
	def userId = column[String]("userId")
	def providerId = column[String]("providerId")
	def email = column[Option[String]]("email")
	def firstName = column[String]("firstName")
	def lastName = column[String]("lastName")
	def fullName = column[String]("fullName")
	def authMethod = column[AuthenticationMethod]("authMethod")
	def avatarUrl = column[Option[String]]("avatarUrl")
	// oAuth 1
	def token = column[Option[String]]("token")
	def secret = column[Option[String]]("secret")
	// oAuth 2
	def accessToken = column[Option[String]]("accessToken")
	def tokenType = column[Option[String]]("tokenType")
	def expiresIn = column[Option[Int]]("expiresIn")
	def refreshToken = column[Option[String]]("refreshToken")
	// Password Info
	def hasher = column[Option[String]]("hasher")
	def password = column[Option[String]]("password")
	def salt = column[Option[String]]("salt")
	// Dates
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("updateDate")

	def * = id ~ userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken ~ hasher ~ password ~ salt ~ creationDate ~ updateDate <> (
		u => User(u._1, (u._2, u._3), u._4, u._5, u._6, u._7, u._8, u._9, (u._10, u._11), (u._12, u._13, u._14, u._15), (u._16, u._17, u._18), u._19, u._20),
		(u: User) => Some((u.id, u.identityId.userId, u.identityId.providerId, u.firstName, u.lastName, u.fullName, u.email, u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret), u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn), u.oAuth2Info.flatMap(_.refreshToken), u.passwordInfo.map(_.hasher), u.passwordInfo.map(_.password), u.passwordInfo.flatMap(_.salt), u.creationDate, u.updateDate)))

	def autoInc = userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken ~ creationDate ~ updateDate returning id

	def insert(t: UserTmp)(implicit s: Session) = t(this.autoInc.insert(t.identityId.userId, t.identityId.providerId, t.firstName, t.lastName, t.fullName, t.email, t.avatarUrl, t.authMethod, t.oAuth1Info.map(_.token), t.oAuth1Info.map(_.secret), t.oAuth2Info.map(_.accessToken), t.oAuth2Info.flatMap(_.tokenType), t.oAuth2Info.flatMap(_.expiresIn), t.oAuth2Info.flatMap(_.refreshToken), t.date, t.date))
		
	def findById(id: UserId)(implicit s: Session) = Query(new UserTable).where(_.id is id).firstOption

	def findByIdentityId(userId: IdentityId)(implicit s: Session): Option[User] = {
		(for {
			user <- (new UserTable)
			if (user.userId is userId.userId) && (user.providerId is userId.providerId)
		} yield user).firstOption
	}

	def findByEmailAndProvider(email: String, providerId: String)(implicit s: Session): Option[User] = {
		(for {
			user <- (new UserTable)
			if (user.email is email) && (user.providerId is providerId)
		} yield user).firstOption
	}

	def all(implicit s: Session) = Query(new UserTable).list

	def save(t: UserTmp)(implicit s: Session) = {
		findByIdentityId(t.identityId) match {
			case None => {
				insert(t)
			}
			case Some(existingUser) => {
				val updatedUser = t(existingUser)
				Query(new UserTable).where(_.id is existingUser.id).update(updatedUser)
				updatedUser
			}
		}
	}

}