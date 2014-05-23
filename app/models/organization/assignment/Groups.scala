package models.organization.assignment

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UserTable
import models.support._
import models.organization._
import models.organization.assignment.table._
import models.question.derivative.table._
import models.question.derivative.Quiz
import controllers.support.PathException

case class Group(id: GroupId, name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) extends HasAccess with HasId[GroupId] with HasCourse {

  def course(implicit session: Session) = assignment.course

  def courseId(implicit session: Session) = course.id

  def section(implicit session: Session) = Sections(sectionId).get

  def assignment(implicit session: Session) = Assignments(assignmentId).get

  def students(implicit session: Session) = Groups.students(id)

  def quizzes(implicit session: Session) = Groups.quizzes(id)

  def quizFor(user: User)(implicit session: Session) = Groups.quizFor(user.id, id)

  def has(quiz: Quiz)(implicit session: Session) = Groups.has(id, quiz.id)

  def access(implicit user: User, session: Session): Access = assignment.access

  def join(implicit user: User, session: Session) = Groups.join(user.id, id)

  def leave(implicit user: User, session: Session) = Groups.leave(user.id, id)

  def enrolled(implicit user: User, session: Session) = students.contains(user)

  def idsMatch(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) = {
    assignmentId == this.assignmentId && sectionId == this.sectionId && courseId == this.courseId
  }
}

object Groups {

  // ======= CREATE ======
  def create(group: Group)(implicit session: Session) = {
    val groupId = (assignmentGroupsTable returning assignmentGroupsTable.map(_.id)) += group
    group.copy(id = groupId)
  }

  // ======= FIND ======
  def apply(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId)(implicit session: Session) : Group = {
    Groups(groupId) match {
      case None => throw new PathException("There was no group for id=["+groupId+"]")
      case Some(group) => {
        if(courseId != group.courseId) throw new PathException("couseId=[" + courseId +"] was not for groupId=["+groupId+"]")
        if(sectionId != group.sectionId) throw new PathException("sectionId=[" + sectionId +"] was not for groupId=["+groupId+"]")
        if(assignmentId != group.assignmentId) throw new PathException("assignmentId=[" + assignmentId +"] was not for groupId=["+groupId+"]")
        group
      }
    }
  }

  def apply(groupId: GroupId)(implicit session: Session) : Option[Group]  = assignmentGroupsTable.where(a => a.id === groupId).firstOption

  def apply(assignmentId: AssignmentId)(implicit session: Session) : List[Group] = assignmentGroupsTable.where(a => a.assignmentId === assignmentId).sortBy(_.name).list

  def apply(sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) : List[Group] = assignmentGroupsTable.where(a => a.sectionId === sectionId && a.assignmentId === assignmentId).sortBy(_.name).list

  def students(groupId: GroupId)(implicit session: Session) =
    (for (
      u <- UserTable.userTable;
      ug <- usersAssignmentGroupsTable if ug.userId === u.id && ug.id === groupId
    ) yield u).sortBy(_.lastName).list

  // ======= ENROLLMENT ======
  def enrolled(userId: UserId, assignmentId: AssignmentId)(implicit session: Session): Option[Group] =
    (for (
      g <- assignmentGroupsTable if g.assignmentId === assignmentId;
      ug <- usersAssignmentGroupsTable if ug.id === g.id && ug.userId === userId
    ) yield g).firstOption

  def join(userId: UserId, groupId: GroupId)(implicit session: Session) = usersAssignmentGroupsTable += User2AssignmentGroup(userId, groupId)

  def leave(userId: UserId, groupId: GroupId)(implicit session: Session) = usersAssignmentGroupsTable.where(r => r.userId === userId && r.id === groupId).delete

  // ======= QUIZZES ======
  def quizzes(assignmentGroupId: GroupId)(implicit session: Session) =
    (for (
      ag2q <- assignmentGroupsQuizzesTable if ag2q.groupId === assignmentGroupId;
      q <- quizzesTable if ag2q.quizId === q.id
    ) yield q).sortBy(_.name).list

  def quizFor(userId: UserId, groupId: GroupId)(implicit session: Session) =
    (for (
      ag2q <- assignmentGroupsQuizzesTable if ag2q.groupId === groupId;
      q <- quizzesTable if ag2q.quizId === q.id && q.owner === userId
    ) yield q).firstOption

  def has(groupId: GroupId, quizId: QuizId)(implicit session: Session) =
    assignmentGroupsQuizzesTable.where(r => r.groupId === groupId && r.quizId === quizId).firstOption.nonEmpty
}
