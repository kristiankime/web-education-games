package models.quiz.question

import com.artclod.mathml.scalar._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import models.quiz.question.QuestionScoring.teacherScore

@RunWith(classOf[JUnitRunner])
class QuestionScoringSpec extends Specification {


	"teacherScore" should {

		"be 1 for correct answer with high difficulty" in { teacherScore(125, true, 100) must beEqualTo(1) }

    "be 0 for incorrect answer with high difficulty" in { teacherScore(125, false, 100) must beEqualTo(0) }

    "be .5 for correct answer with half difficulty" in { teacherScore(50, true, 80) must beEqualTo(.5) }

    "be .5 for incorrect answer with half difficulty" in { teacherScore(50, false, 80) must beEqualTo(.5) }

    "be .1 for correct answer with easy difficulty" in { teacherScore(10, true, 80) must beEqualTo(.1) }

    "be .9 for incorrect answer with easy difficulty" in { teacherScore(10, false, 80) must beEqualTo(.9) }

  }

}
