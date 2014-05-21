package models.question.derivative

import models.support.CourseId
import models.support.UserId
import org.joda.time.DateTime
import models.support.QuestionId
import com.artclod.mathml.scalar._

object QuestionTmpTest {
	def apply(owner: UserId,
		mathML: MathMLElem = `6`,
		rawStr: String = "6",
		creationDate: DateTime = DateTime.now) =
		Question(null, owner, mathML, rawStr, creationDate)
}