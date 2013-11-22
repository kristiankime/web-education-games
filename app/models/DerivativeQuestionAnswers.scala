package models

import scala.collection.mutable.LinkedHashMap
import mathml.scalar.MathMLElem
import scala.xml.XML
import mathml.MathML

case class DerivativeQuestionAnswer(question: DerivativeQuestion, id: Int, rawStr: String, mathML: MathMLElem, synched: Boolean, correct: Boolean)

object DerivativeQuestionAnswers {
	private var idCounter = 0
	private val derivativeQuestionAnswers = LinkedHashMap[Int, LinkedHashMap[Int, DerivativeQuestionAnswer]]()

	private def nextId = {
		val id = idCounter
		idCounter += 1
		id
	}

	def all() = derivativeQuestionAnswers.values.toList

	def create(question: DerivativeQuestion, rawStr: String, mathML: MathMLElem, synched: Boolean) = {
		val correct = MathML.checkEq("x", question.mathML.d("x"), mathML)
		val answer = DerivativeQuestionAnswer(question, nextId, rawStr, mathML, synched, correct)
		derivativeQuestionAnswers.getOrElseUpdate(question.id, LinkedHashMap()).put(answer.id, answer)
		answer
	}

	def read(qid: Int, aid: Int) = derivativeQuestionAnswers.get(qid).map(_.get(aid)).flatten

	def delete(qid: Int) = derivativeQuestionAnswers.remove(qid)

}
