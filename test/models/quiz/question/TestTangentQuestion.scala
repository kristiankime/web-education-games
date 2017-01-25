package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.quiz.answer.{DerivativeAnswers, TestDerivativeAnswer}
import models.support.UserId
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session

object TestTangentQuestion {

	def apply(owner: UserId, mathML: MathMLElem = `6`, rawStr: String = "6", xMathML: MathMLElem = `1`, xRawStr: String = "1", creationDate: DateTime = JodaUTC.zero, difficulty: Double = 0d) =
		TangentQuestion(null, owner, mathML, rawStr, xMathML, xRawStr, creationDate, difficulty)

//	def create(owner: UserId, mathML: MathMLElem = `6`, text: String = "6", creationDate: DateTime = JodaUTC.zero, difficulty: Double = 0d, answered : Option[UserId] = None)(implicit session: Session) = {
//		val question = DerivativeQuestions.create(DerivativeQuestion(null, owner, mathML, text, creationDate, difficulty))
//		val answer = answered match {
//			case None => None
//			case Some(user) => Some(DerivativeAnswers.createAnswer(TestDerivativeAnswer(user, question.id, correct = true, creationDate = creationDate)))
//		}
//		(question, answer)
//	}

}