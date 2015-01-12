package models

import play.api.db.slick.Config.driver.simple.Session

package object organization {

  trait HasCourse {
    def course(implicit session: Session) : Course
  }

  def courseFrom(e: Either[Course, HasCourse])(implicit session: Session) = e match {
    case Left(c) => c
    case Right(h) => h.course
  }

}
