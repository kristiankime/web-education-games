package service.table

import play.api.db.slick.Config.driver.simple._
import securesocial.core.{AuthenticationMethod, IdentityId, OAuth1Info, OAuth2Info, PasswordInfo}

import scala.language.implicitConversions

object SecurityMapper {

	implicit def string2AuthenticationMethod = MappedColumnType.base[AuthenticationMethod, String](
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

	implicit def tuple2PasswordInfo(tuple: (Option[String], Option[String], Option[String])): Option[PasswordInfo] = tuple match {
		case (Some(hasher), Some(password), salt) => Some(PasswordInfo(hasher, password, salt))
		case _ => None
	}
	
}