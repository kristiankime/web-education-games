package com.artclod.securesocial

import service.table.UserTable
import scala.slick.lifted.Query
import models.id.UserId
import play.api.db.slick.DB
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import securesocial.core.Identity
import securesocial.core.Authenticator
import play.api.mvc.Cookie

// modified from http://stackoverflow.com/questions/17735636/testing-a-play2-application-with-securesocial-using-dependency-injection
object TestUtils {

	@inline implicit def loggedInFakeRequestWrapper[T](x: FakeRequest[T]) = new LoggedInFakeRequest(x)

	final class LoggedInFakeRequest[T](val self: FakeRequest[T]) extends AnyVal {
		def withLoggedInUser(identity: Identity) = {
//			val userToLogInAs: Identity = getUser(id)
			val cookie: Cookie = Authenticator.create(identity) match {
				case Right(authenticator) => authenticator.toCookie
				case _ => throw new IllegalStateException("Coding error this should never happen")
			}
			self.withCookies(cookie)
		}
	}

}