package models

import scala.collection.mutable.LinkedHashMap
import mathml.MathMLElem
import scala.xml.XML
import mathml.MathML

case class DerivativeQuestionAnswer(question: DerivativeQuestion, id: Int, originalStr: String, mathML: MathMLElem, correct: Boolean)

object DerivativeQuestionAnswers {
	private var idCounter = 0
	private val derivativeQuestionAnswers = LinkedHashMap[Int, LinkedHashMap[Int, DerivativeQuestionAnswer]]()

	def all() = derivativeQuestionAnswers.values.toList

	def create(question: DerivativeQuestion, answerStr: String) = {
		val answerMathML = MathML(XML.loadString(answerStr)).get // TODO can fail here
		val correct = MathML.simplifyEquals(question.mathML.derivative("x"), answerMathML)
		val answer = DerivativeQuestionAnswer(question, idCounter, answerStr, answerMathML, correct)
		idCounter += 1

		if (derivativeQuestionAnswers.get(question.id).isEmpty) {
			derivativeQuestionAnswers.put(question.id, LinkedHashMap())
		}
		derivativeQuestionAnswers.get(question.id).get.put(answer.id, answer)
		answer
	}

	def removeQuestion(qid: Int) = derivativeQuestionAnswers.remove(qid)

	def get(qid: Int, aid: Int) = derivativeQuestionAnswers.get(qid).map(_.get(aid)).flatten
}
