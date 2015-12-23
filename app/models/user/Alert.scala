package models.user

import play.api.templates.Html

object Alert {

  def alertsFor(user: User) : Option[Html] = Some(Html("test"))

}
