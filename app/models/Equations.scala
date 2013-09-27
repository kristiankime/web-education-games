package models

import scala.collection.mutable.LinkedHashMap

case class Equation(id: Int, equation: String)

object Equations {
	private var idCounter = 0
	private val equations = LinkedHashMap[Int, Equation]()
	
	def all() = equations.values.toList

	def create(equation: String) = {
		val id = idCounter
		idCounter += 1
		val eq = Equation(id, equation)
		equations.put(id, eq)
		eq
	}

	def delete(id: Int) {
		equations.remove(id)
	}
}
