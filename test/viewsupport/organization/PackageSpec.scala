package viewsupport.organization

import models.quiz.Correct2Short
import models.quiz.answer.{TestDerivativeAnswer, DerivativeAnswer}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
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
			mathMLStr(Some(Left(TestDerivativeAnswer(UserId(0), QuestionId(0), rawStr="rawStr")))) must beEqualTo(Some("rawStr"))
		}

		"get math string from Answer" in {
			mathMLStr(Some(Right(DerivativeAnswer(AnswerId(0), UserId(0), QuestionId(0), Cn(0), "rawStr", Correct2Short(false), JodaUTC.now)))) must beEqualTo(Some("rawStr"))
		}
	}
	
}
