package viewsupport

import models.question.derivative._
import models.organization.assignment.Assignment

package object organization {

	def mathMLStr(currentAnswer: Option[Either[Answer, Answer]]) : Option[String] = currentAnswer.map {
		_ match {
			case Right(ans) => ans.rawStr
			case Left(ans) => ans.rawStr
		}
	}

}