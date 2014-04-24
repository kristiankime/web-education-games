package viewsupport

import models.question.derivative._
import models.organization.assignment.Assignment

package object organization {

  type Course = models.organization.Course
  type Section = models.organization.Section
  type Assignment = models.organization.assignment.Assignment

	def mathMLStr(currentAnswer: Option[Either[AnswerTmp, Answer]]) : Option[String] = currentAnswer.map {
		_ match {
			case Right(tmp) => tmp.rawStr
			case Left(ans) => ans.rawStr
		}
	}

}