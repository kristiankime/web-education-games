package models

import scala.collection.mutable.LinkedHashMap

case class Equation(id: Int, user:String, text: String, equation: String)

object Equations {
	private val equations = LinkedHashMap[Int, Equation]()
	
	def all() = equations.values.toList

	def create(user:String, text: String, equation: String) = {
		val id = equations.size;
		val eq = Equation(id, user, text, equation);
		equations.put(id, eq)
		eq
	}

	def delete(id: Int) {
		equations.remove(id)
	}

}
