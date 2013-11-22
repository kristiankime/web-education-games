package models

import scala.collection.mutable.LinkedHashMap
import scala.xml.XML
import mathml._
import mathml.scalar._

case class DerivativeQuestion(id: Int, mathML: MathMLElem, rawStr: String, synched: Boolean)

object DerivativeQuestions {
	private var idCounter = 0
	private val derivativeQuestions = LinkedHashMap[Int, DerivativeQuestion]()

	// Boot the system up with default question(s)
	DerivativeQuestions.create(Math(x), "x", true)
	
	private def nextId = { 
		val id = idCounter
		idCounter += 1
		id
	}
	
	def all() = derivativeQuestions.values.toList

	def create(mathML: MathMLElem, rawStr: String, synched: Boolean) = {
		val question = DerivativeQuestion(nextId, mathML, rawStr, synched)
	    
		derivativeQuestions.put(question.id, question)
		question
	}
	
	def read(id: Int) = derivativeQuestions.get(id)
	
	def delete(id: Int) = {
		DerivativeQuestionAnswers.delete(id)
		derivativeQuestions.remove(id)
	}

}
