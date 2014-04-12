package models.question.derivative

import models.organization.SectionTmp
import models.support.CourseId
import models.support.UserId
import org.joda.time.DateTime
import models.support.QuestionId
import mathml.scalar.MathMLElem

object QuestionTmpTest {
	def apply(owner: UserId,
		mathML: MathMLElem = mathml.scalar.`6`,
		rawStr: String = "6",
		creationDate: DateTime = DateTime.now) =
		QuestionTmp(owner, mathML, rawStr, creationDate)
}