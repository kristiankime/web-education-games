package models.organization.view

import play.api.db.slick.Config.driver.simple._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import scala.slick.session.Session
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import models.DBTest
import service.UserTmpTest
import models.organization._
import models.organization.table._
import service._
import service.table._
import models.question.derivative.AnswerTmpTest
import models.id._
import mathml.Math
import models.question.derivative.Answer
import mathml.scalar.Cn
import org.joda.time.DateTime

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification {

	"mathMLStr" should {
		
		"get None from None" in {
			mathMLStr(None) must beNone
		}
	
		"get math string from AnswerTmp" in {
			mathMLStr(Some(Left(AnswerTmpTest(UserId(0), QuestionId(0), rawStr="rawStr")))) must beEqualTo(Some("rawStr"))
		}

		"get math string from Answer" in {
			mathMLStr(Some(Right(Answer(AnswerId(0), UserId(0), QuestionId(0), Cn(0), "rawStr", false, DateTime.now)))) must beEqualTo(Some("rawStr"))
		}
	}
	
}
