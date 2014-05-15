package models

import models.support.CourseId
import scala.slick.session.Session

package object organization {

  trait HasCourse {
    def course(implicit session: Session) : Course
  }

  def courseFrom(e: Either[Course, HasCourse])(implicit session: Session) = e match {
    case Left(c) => c
    case Right(h) => h.course
  }

}
