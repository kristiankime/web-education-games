package models.quiz.question

import com.artclod.javascript.RunJS
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.quiz.answer.{DerivativeAnswers, TestDerivativeAnswer}
import models.support.UserId
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session

object TestDerivativeQuestion {

	def apply(owner: UserId,
		mathML: MathMLElem = `6`,
		rawStr: String = "6",
		creationDate: DateTime = JodaUTC.zero) =
		DerivativeQuestion(null, owner, mathML, rawStr, creationDate, 0d)

	def create(owner: UserId, mathML: MathMLElem = `6`, text: String = "6", creationDate: DateTime = JodaUTC.zero, difficulty: Double = 0d, answered : Option[UserId] = None)(implicit session: Session) = {
		val question = DerivativeQuestions.create(DerivativeQuestion(null, owner, mathML, text, creationDate, difficulty))
		val answer = answered match {
			case None => None
			case Some(user) => Some(DerivativeAnswers.createAnswer(TestDerivativeAnswer(user, question.id, correct = true, creationDate = creationDate)))
		}
		(question, answer)
	}

}