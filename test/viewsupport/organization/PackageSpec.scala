package viewsupport.organization

import models.question.Correct2Short
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import models.question.derivative.{TestAnswer, Answer}
import models.support._
import com.artclod.mathml.scalar.Cn
import com.artclod.slick.JodaUTC

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification {

	"mathMLStr" should {
		
		"get None from None" in {
			mathMLStr(None) must beNone
		}
	
		"get math string from AnswerTmp" in {
			mathMLStr(Some(Left(TestAnswer(UserId(0), QuestionId(0), rawStr="rawStr")))) must beEqualTo(Some("rawStr"))
		}

		"get math string from Answer" in {
			mathMLStr(Some(Right(Answer(AnswerId(0), UserId(0), QuestionId(0), Cn(0), "rawStr", Correct2Short(false), JodaUTC.now)))) must beEqualTo(Some("rawStr"))
		}
	}
	
}
