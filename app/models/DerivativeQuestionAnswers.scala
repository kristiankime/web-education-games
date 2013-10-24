package models

import scala.collection.mutable.LinkedHashMap
import mathml.MathMLElem
import scala.xml.XML
import mathml.MathML

case class DerivativeQuestionAnswer(qid: Int, aid: Int, mathML: MathMLElem)

object DerivativeQuestionAnswers {
	private var idCounter = 0
	private val derivativeQuestionAnswers = LinkedHashMap[Int, LinkedHashMap[Int, DerivativeQuestionAnswer]]()
	
	def all() = derivativeQuestionAnswers.values.toList

	def create(qid: Int, str: String) = {
		val aid = idCounter
		idCounter += 1
		val mathML = MathML(XML.loadString(str))
		val question = DerivativeQuestionAnswer(qid, aid, mathML.get)
		
		if(derivativeQuestionAnswers.get(qid).isEmpty){
			derivativeQuestionAnswers.put(qid, LinkedHashMap())
		}
		derivativeQuestionAnswers.get(qid).get.put(aid, question)
		question
	}
	
	def removeQuestion(qid: Int) = derivativeQuestionAnswers.remove(qid)
	
	def get(qid: Int, aid: Int) = derivativeQuestionAnswers.get(qid).map(_.get(aid).getOrElse(null))
}
