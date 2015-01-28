package models.organization

import models.quiz.Quizzes
import models.user.{UserFull, UserSetting}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import models.support._
import models.organization.table._
import service._
import models.user.table.userSettingsTable

case class Course(id: CourseId, name: String, organizationId: OrganizationId, ownerId: UserId, editCode: String, viewCode: Option[String], creationDate: DateTime, updateDate: DateTime) extends Secured with HasId[CourseId] {

  def organization(implicit session: Session) = Organizations(organizationId).get

  def quizzes(implicit session: Session) = Quizzes(id)

  def students(implicit session: Session) = Courses.students(id)

	def anyStudent = viewCode.isEmpty

  // ========== Access Methods ==========
  protected def linkAccess(implicit user: UserSetting, session: Session): Access = Courses.otherAccess(this)

	/**
	 * In terms of access level Users can:
	 *     1) Own the Course which grants Own access
	 *     2) Have association access granted at either the Edit or View
	 * Access to the course determines access to quizzes etc.
	 * This means users who are granted access to Sections should also be granted access to the corresponding course.
	 */
	def access(implicit user: UserSetting, session: Session): Access = directAccess

	def grantAccess(access: Access)(implicit user: UserSetting, session: Session) = Courses.grantAccess(this, access)
}

object Courses {
	// ======= CREATE ======
	def create(course: Course)(implicit session: Session) = {
    val courseId = (coursesTable returning coursesTable.map(_.id)) += course
    course.copy(id = courseId)
  }

	// ======= FIND ======
	def apply(id: CourseId)(implicit session: Session) = coursesTable.where(_.id === id).firstOption

	def apply(userId: UserId)(implicit session: Session) = {
		(for (
			uc <- usersCoursesTable if uc.userId === userId;
			c <- coursesTable if uc.id === c.id
		) yield c)
			.union(
				coursesTable.where(_.owner === userId)).list
	}

  def list(implicit session: Session) = coursesTable.list

  def list(organizationId: OrganizationId)(implicit session: Session) = coursesTable.where(_.organization === organizationId).list

  def students(courseId: CourseId)(implicit session: Session) =
  (for (uc <- usersCoursesTable if uc.id === courseId && uc.access === View.asInstanceOf[Access];
        u <- userSettingsTable if u.userId === uc.userId
  ) yield u).sortBy(_.name).list

  def studentsExcept(courseId: CourseId, userId: UserId)(implicit session: Session) =
   (for (uc <- usersCoursesTable if uc.id === courseId && uc.access === View.asInstanceOf[Access];
         u <- userSettingsTable if (u.userId === uc.userId) && (u.userId =!= userId)
   ) yield u).sortBy(_.name).list


  // ======= AUTHORIZATION ======
	def otherAccess(course: Course)(implicit user: UserSetting, session: Session) =
    usersCoursesTable.where(uc => uc.userId === user.id && uc.id === course.id).firstOption.map(_.access).toAccess

	def grantAccess(course: Course, access: Access)(implicit user: UserSetting, session: Session) {
		if (course.access < access) {
      usersCoursesTable.where(uc => uc.userId === user.id && uc.id === course.id).firstOption match {
				case Some(u2c) if u2c.access < access => usersCoursesTable.where(_.id === course.id).update(User2Course(user.id, course.id, access, 1))
				case None => usersCoursesTable.insert(User2Course(user.id, course.id, access, 1))
				case _ => {}
			}
		}
	}

}
