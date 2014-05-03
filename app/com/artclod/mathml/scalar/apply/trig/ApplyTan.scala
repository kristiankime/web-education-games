package com.artclod.mathml.scalar.apply.trig

import scala.util._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.concept._

case class ApplyTan(value: MathMLElem) extends UnaryFunction(value, Tan) {

	override def eval(b: Map[String, Double]) = Try(math.tan(v.eval(b).get))

	override def constant: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.tan(v))
		case _ => None
	}

	def simplifyStep() = ApplyTan(v.s)

	def derivative(x: String) = (ApplySec(v.s) ^ `2`) * v.d(x)

}