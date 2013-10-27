package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._

case class DerivativeQuestion(id: Int, mathML: MathMLElem)

object DerivativeQuestions {
	private var idCounter = 0
	private val derivativeQuestions = LinkedHashMap[Int, DerivativeQuestion]()

	// Boot the system up with default question(s)
	DerivativeQuestions.create(Math(Ci("x")).toString)
	
	def all() = derivativeQuestions.values.toList

	def create(str: String) = {
		val id = idCounter
		idCounter += 1
		val mathML = MathML(XML.loadString(str))
		val question = DerivativeQuestion(id, mathML.get)
		derivativeQuestions.put(id, question)
		question
	}

	def delete(id: Int) = {
		derivativeQuestions.remove(id)
		DerivativeQuestionAnswers.removeQuestion(id)
	}
	
	def get(id: Int) = derivativeQuestions.get(id)
}
