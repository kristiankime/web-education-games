package models.question.derivative

import com.artclod.slick.JodaUTC
import models.DBTest._
import models.organization._
import models.support.CourseId
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test._
import service._
import viewsupport.question.derivative._
import com.artclod.mathml.scalar._

@RunWith(classOf[JUnitRunner])
class QuestionDifficultySpec extends Specification {

  private implicit class QD(eq: MathMLElem){
    def difficulty = QuestionDifficulty(eq)
  }

	"difficulty" should {

		"be 1 for constant" in { `4`.difficulty must beEqualTo(1) }

    "be 1 for variable" in { x.difficulty must beEqualTo(1) }

    "be 2 for x^n" in { (x ^ 3).difficulty must beEqualTo(2) }

    "be 2 for x * n" in { (x * 3).difficulty must beEqualTo(2) }

    "be 2 for x * x" in { (x * x).difficulty must beEqualTo(2) }
  }

}
