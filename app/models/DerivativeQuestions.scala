package models

import scala.collection.mutable.LinkedHashMap
import mathml.MathMLElem
import scala.xml.XML
import mathml.MathML

case class DerivativeQuestion(id: Int, mathML: MathMLElem)

object DerivativeQuestions {
	private var idCounter = 0
	private val derivativeQuestions = LinkedHashMap[Int, DerivativeQuestion]()
	
	def all() = derivativeQuestions.values.toList

	def create(str: String) = {
		val id = idCounter
		idCounter += 1
		val xml = XML.loadString(str)
		val mathML = MathML(XML.loadString(str))
		val question = DerivativeQuestion(id, mathML.get)
		derivativeQuestions.put(id, question)
		question
	}

	def delete(id: Int) = derivativeQuestions.remove(id)
}
