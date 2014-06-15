package models

import com.artclod.slick.Joda
import models.question.derivative.table._
import models.support.QuestionId
import org.joda.time.DateTime
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication
import play.api.Play.current
import play.api.db.slick.DB
import service.table.UsersTable
import service.UserTmpTest
import service.User
import play.api.db.slick.Config.driver.simple._


object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

	def newFakeUser(user: User)(implicit session: Session): User = UsersTable.insert(user)

	def newFakeUser(implicit session: Session): User = newFakeUser(UserTmpTest())

  def fakeStartWorkingOn(user: User, questionId: QuestionId, time: DateTime)(implicit session: Session) ={
    answerTimesTable += AnswerTime(user.id, questionId, time)
    time
  }
}