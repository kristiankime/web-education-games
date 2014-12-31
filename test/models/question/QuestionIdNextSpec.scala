package models.question

import com.artclod.slick.JodaUTC
import models.DBTest
import models.DBTest._
import models.question.derivative.DerivativeAnswers.AnswersSummary
import models.question.table.QuestionIdNext
import models.support._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._

@RunWith(classOf[JUnitRunner])
class QuestionIdNextSpec extends Specification {

  "apply" should {

    "return a unique id" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        QuestionIdNext.apply must beEqualTo(QuestionId(1))
      }
    }

    "return two unique ids" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        QuestionIdNext.apply must beEqualTo(QuestionId(1))
        QuestionIdNext.apply must beEqualTo(QuestionId(2))
      }
    }

    "return range of unique ids" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      Range(1, 10).par.map(v => DB.withSession { implicit session: Session => QuestionIdNext.apply }).toList.sortWith(_.v < _.v) must beEqualTo(Range(1, 10).map(QuestionId(_)).toList)
    }
  }

}