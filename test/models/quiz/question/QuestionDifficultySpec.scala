package models.quiz.question

import com.artclod.mathml.scalar._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class QuestionDifficultySpec extends Specification {

  private implicit class QD(eq: MathMLElem){
    def difficulty = DerivativeDifficulty(eq)
  }

	"difficulty" should {

		"be 1 for constant" in { `4`.difficulty must beEqualTo(1) }

    "be 1 for variable" in { x.difficulty must beEqualTo(1) }

    "be 2 for x^n" in { (x ^ 3).difficulty must beEqualTo(2) }

    "be 2 for x * n" in { (x * 3).difficulty must beEqualTo(2) }

    "be 2 for x * x" in { (x * x).difficulty must beEqualTo(2) }
  }

}
