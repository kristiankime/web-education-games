package mathml.scalar.concept

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._

abstract class Logarithm(val b: BigDecimal, val v: MathMLElem, pre: MathMLElem*)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (pre ++ v): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(math.log(v.eval(boundVariables).get) / math.log(b.doubleValue))

	def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Logarithm.log(b, v))
		case _ => None
	}

	def variables: Set[String] = v.variables
	
}

object Logarithm {

	def ln(value: Constant) = value match {
		case v: ConstantInteger => Cn(math.log(v.v.doubleValue))
		case v: ConstantDecimal => Cn(math.log(v.v.doubleValue))
	}

	def log10(value: Constant) = value match {
		case v: ConstantInteger => Cn(math.log10(v.v.doubleValue))
		case v: ConstantDecimal => Cn(math.log10(v.v.doubleValue))
	}

	def log(b: BigDecimal, value: Constant) = value match {
		case v: ConstantInteger => Cn(math.log(v.v.doubleValue) / math.log(b.doubleValue))
		case v: ConstantDecimal => Cn(math.log(v.v.doubleValue) / math.log(b.doubleValue))
	}

}