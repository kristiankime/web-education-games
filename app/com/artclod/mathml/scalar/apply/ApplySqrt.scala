package com.artclod.mathml.scalar.apply

import com.artclod.mathml.scalar.{Root, _}
import com.artclod.mathml.scalar.concept._

import scala.util._

case class ApplySqrt(value: MathMLElem) extends NthRoot(2, value, Seq(Root): _*) {

	override def eval(boundVariables: Map[String, Double]) = Try(math.sqrt(v.eval(boundVariables).get))

	override def constant: Option[Constant] = v.c match {
		case Some(v) => Some(NthRoot.sqrt(v))
		case _ => None
	}

	def simplifyStep() = ApplySqrt(v.s)

	def derivative(x: String) = {
		val f = v.s
		val fP = f.d(x)

		fP / (`2` * ApplySqrt(f))
	}

	def toText: String = "sqrt(" + value.toText + ")"
}