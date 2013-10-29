package mathml.scalar

import scala.util._
import scala.xml._
import mathml._


case class ApplyDivide(val numerator: MathMLElem,val denominator: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Divide()) ++ numerator ++ denominator): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(numerator.eval(boundVariables).get / denominator.eval(boundVariables).get)
	
	def cn: Option[Cn] = (numerator.cn, denominator.cn) match {
		case (Some(nu), Some(de)) => Some(nu / de)
		case _ => None
	}
	
	def simplify() = {
		if (cn.nonEmpty) cn.get
		else ApplyDivide(numerator.simplify, denominator.simplify)
	}

	def variables: Set[String] = numerator.variables ++ denominator.variables
	
	// User the quotient rule (http://en.wikipedia.org/wiki/Quotient_rule)
	def derivative(wrt: String): MathMLElem = {
		val f = numerator
		val fP = f.d(wrt).simplify
		val g = denominator
		val gP = g.d(wrt).simplify
		
		// (f/g)' = (f'g - g'f)/g^2
		(fP*g - gP*f) / g^Cn(2) simplify
	}
}