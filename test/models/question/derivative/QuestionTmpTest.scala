package models.question.derivative

import models.organization.SectionTmp
import models.id.CourseId
import models.id.UserId
import org.joda.time.DateTime
import models.id.QuestionId
import mathml.scalar.MathMLElem

object QuestionTmpTest {
	def apply(owner: UserId,
		mathML: MathMLElem = mathml.scalar.`6`,
		rawStr: String = "6",
		creationDate: DateTime = DateTime.now) =
		QuestionTmp(owner, mathML, rawStr, creationDate)
}