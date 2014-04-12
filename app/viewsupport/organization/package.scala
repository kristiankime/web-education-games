package models.organization

import models.question.derivative._

package object view {

	def mathMLStr(currentAnswer: Option[Either[AnswerTmp, Answer]]) : Option[String] = currentAnswer.map {
		_ match {
			case Right(tmp) => tmp.rawStr
			case Left(ans) => ans.rawStr
		}
	}

}