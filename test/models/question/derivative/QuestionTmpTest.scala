package models.question.derivative

import models.support.UserId
import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import com.artclod.slick.Joda

object QuestionTmpTest {
	def apply(owner: UserId,
		mathML: MathMLElem = `6`,
		rawStr: String = "6",
		creationDate: DateTime = Joda.now) =
		Question(null, owner, mathML, rawStr, creationDate)
}