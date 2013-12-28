package models

import securesocial.core._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.Identity
import securesocial.core.IdentityId
import securesocial.core.OAuth1Info
import play.api.Play.current

// Adapted from http://blog.lunatech.com/2013/07/04/play-securesocial-slick
case class User(uid: Long,
	identityId: IdentityId,
	firstName: String,
	lastName: String,
	fullName: String,
	email: Option[String],
	avatarUrl: Option[String],
	authMethod: AuthenticationMethod,
	oAuth1Info: Option[OAuth1Info],
	oAuth2Info: Option[OAuth2Info],
	passwordInfo: Option[PasswordInfo] = None) extends Identity

object UsersTable extends Table[User]("user") {

	def uid = column[Long]("id", O.PrimaryKey, O.AutoInc)
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
	// TODO add password info

	def * = uid ~ userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken <> (
		u => User(u._1, (u._2, u._3), u._4, u._5, u._6, u._7, u._8, u._9, (u._10, u._11), (u._12, u._13, u._14, u._15)),
		(u: User) => Some((u.uid, u.identityId.userId, u.identityId.providerId, u.firstName, u.lastName, u.fullName, u.email, u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret), u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn), u.oAuth2Info.flatMap(_.refreshToken))))

	def autoInc = userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~ secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken returning uid

	implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
		authenticationMethod => authenticationMethod.method,
		string => AuthenticationMethod(string))

	implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])): Option[OAuth1Info] = tuple match {
		case (Some(token), Some(secret)) => Some(OAuth1Info(token, secret))
		case _ => None
	}

	implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])): Option[OAuth2Info] = tuple match {
		case (Some(token), tokenType, expiresIn, refreshToken) => Some(OAuth2Info(token, tokenType, expiresIn, refreshToken))
		case _ => None
	}

	implicit def tuple2IdentityId(tuple: (String, String)): IdentityId = tuple match {
		case (userId, providerId) => IdentityId(userId, providerId)
	}

	def findById(id: Long)(implicit s: Session) = Query(UsersTable).where(_.uid is id).firstOption

	def findByIdentityId(userId: IdentityId)(implicit s: Session): Option[User] = {
		val q = for {
			user <- UsersTable
			if (user.userId is userId.userId) && (user.providerId is userId.providerId)
		} yield user

		q.firstOption
	}

	def all(implicit s: Session) = {
		val q = for { user <- UsersTable } yield user
		q.list
	}

	def save(i: Identity)(implicit s: Session): User = this.save(i.identityId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo)

	def save(identityId: IdentityId, firstName: String, lastName: String, fullName: String, email: Option[String], avatarUrl: Option[String], authMethod: AuthenticationMethod, oAuth1Info: Option[OAuth1Info], oAuth2Info: Option[OAuth2Info], passwordInfo: Option[PasswordInfo])(implicit s: Session) = {
		findByIdentityId(identityId) match {
			case None => {
				val uid = this.autoInc.insert(identityId.userId, identityId.providerId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info.map(_.token), oAuth1Info.map(_.secret), oAuth2Info.map(_.accessToken), oAuth2Info.flatMap(_.tokenType), oAuth2Info.flatMap(_.expiresIn), oAuth2Info.flatMap(_.refreshToken))
				Query(UsersTable).where(_.uid is uid).first
			}
			case Some(existingUser) => {
				val userRow = for { u <- UsersTable if u.uid is existingUser.uid } yield u
				val updatedUser = User(existingUser.uid, identityId, firstName, lastName, fullName, email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo)
				userRow.update(updatedUser)
				updatedUser
			}
		}
	}

}