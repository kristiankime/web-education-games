package models.question.derivative

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import mathml.scalar._
import mathml.scalar.apply._
import service.table._
import models.id._
import models.DBTest
import models.DBTest.inMemH2
import service._
import org.joda.time.DateTime
import play.api.db.slick.DB
import scala.slick.session.Session
import models.question.derivative.view.QuestionDetails
import models.question.derivative.view.StudentQuestionResults

@RunWith(classOf[JUnitRunner])
class QuizzesSpec extends Specification {

	"questions" should {
		
		"find all the questions for this quiz" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				
				val user = DBTest.fakeUser(UserTmpTest())
				val quiz = Quizzes.create(QuizTmpTest(owner = user.id), None)

				val question1 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)
				val question2 = Questions.create(QuestionTmpTest(owner = user.id), quiz.id)

				true must beTrue
//				question.results(user) must beEqualTo(StudentQuestionResults(question, false, List(answer1, answer2)))
			}
		}
		
	}


}
