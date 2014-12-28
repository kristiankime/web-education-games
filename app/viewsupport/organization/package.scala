package viewsupport

import models.question.derivative._

package object organization {

	def mathMLStr(currentAnswer: Option[Either[DerivativeAnswer, DerivativeAnswer]]) : Option[String] = currentAnswer.map {
		_ match {
			case Right(ans) => ans.rawStr
			case Left(ans) => ans.rawStr
		}
	}

}