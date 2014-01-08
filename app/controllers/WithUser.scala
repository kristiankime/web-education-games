package controllers

import securesocial.core.SecuredRequest
import play.api.mvc.AnyContent
import play.api.mvc.Result
import service.User

object WithUser {
	// : SecuredRequest[AnyContent] => Result
//	def apply(block: (SecuredRequest[AnyContent], User) => Result)(implicit request: SecuredRequest[AnyContent]) = {
//		request.user match {
//			case user: User => {
//			  block.apply(request, user)
//			}
//			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // TODO better handling then just throwing 
//		}
//	}
	
		def apply(block: SecuredRequest[AnyContent] => User => Result)(implicit request: SecuredRequest[AnyContent]) = {
		request.user match {
			case user: User => {
			  block.apply(request).apply(user)
			}
			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // TODO better handling then just throwing 
		}
	}
}