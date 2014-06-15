package models.organization.assignment

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UsersTable
import models.support._
import models.organization._
import models.organization.assignment.table._
import models.question.derivative.table._
import models.question.derivative.Quiz

case class Group(id: GroupId, name: String, sectionId: SectionId, assignmentId: AssignmentId, creationDate: DateTime, updateDate: DateTime) extends HasAccess with HasId[GroupId] with HasCourse {

  // Organization
  def course(implicit session: Session) = assignment.course

  def courseId(implicit session: Session) = course.id

  def section(implicit session: Session) = Sections(sectionId).get

  def assignment(implicit session: Session) = Assignments(assignmentId).get

  def students(implicit session: Session) = Groups.students(id)

  // Quizzes
  def quizzes(implicit session: Session) = Groups.quizzes(id)

  def quizFor(user: User)(implicit session: Session) = Groups.quizFor(user.id, id)

  def has(quiz: Quiz)(implicit session: Session) = Groups.hasQuiz(id, quiz.id)

  // Access
  def access(implicit user: User, session: Session): Access = assignment.access

  def join(implicit user: User, session: Session) = Groups.join(user.id, id)

  def leave(implicit user: User, session: Session) = Groups.leave(user.id, id)

  def enrolled(implicit user: User, session: Session) = students.contains(user)
  
}

object Groups {

  // ======= CREATE ======
  def create(group: Group)(implicit session: Session) = {
    val groupId = (assignmentGroupsTable returning assignmentGroupsTable.map(_.id)) += group
    group.copy(id = groupId)
  }

  // ======= FIND ======
  def apply(groupId: GroupId)(implicit session: Session) : Option[Group]  = assignmentGroupsTable.where(a => a.id === groupId).firstOption

  def apply(assignmentId: AssignmentId)(implicit session: Session) : List[Group] = assignmentGroupsTable.where(a => a.assignmentId === assignmentId).sortBy(_.name).list

  def apply(sectionId: SectionId, assignmentId: AssignmentId)(implicit session: Session) : List[Group] = assignmentGroupsTable.where(a => a.sectionId === sectionId && a.assignmentId === assignmentId).sortBy(_.name).list

  def apply(questionId: QuestionId)(implicit session: Session) = //questionsForTable.where(_.questionId === questionId).firstOption
    (for (
      g <- assignmentGroupsTable;
      qf <- questionsForTable if qf.questionId === questionId && qf.groupId === g.id
    ) yield g).firstOption

  def students(groupId: GroupId)(implicit session: Session) =
    (for (
      u <- UsersTable.userTable;
      ug <- usersAssignmentGroupsTable if ug.userId === u.id && ug.id === groupId
    ) yield u).sortBy(_.lastName).list

  // ======= ENROLLMENT ======
  def enrolled(userId: UserId, assignmentId: AssignmentId)(implicit session: Session): Option[Group] =
    (for (
      g <- assignmentGroupsTable if g.assignmentId === assignmentId;
      ug <- usersAssignmentGroupsTable if ug.id === g.id && ug.userId === userId
    ) yield g).firstOption

  def join(userId: UserId, groupId: GroupId)(implicit session: Session) = usersAssignmentGroupsTable += User2Group(userId, groupId)

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
      q <- quizzesTable if ag2q.quizId === q.id && q.ownerId === userId
    ) yield q).firstOption

  def hasQuiz(groupId: GroupId, quizId: QuizId)(implicit session: Session) =
    assignmentGroupsQuizzesTable.where(r => r.groupId === groupId && r.quizId === quizId).firstOption.nonEmpty
}
