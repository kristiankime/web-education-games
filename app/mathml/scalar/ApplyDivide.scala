package mathml.scalar

import scala.util._
import scala.xml._
import mathml._


case class ApplyDivide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val divide: Divide,
	val numerator: MathMLElem,
	val denominator: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](divide) ++ numerator ++ denominator): _*) {

	def this(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, divide, numerator, denominator)

	def eval(boundVariables: Map[String, Double]) = Try(numerator.eval(boundVariables).get / denominator.eval(boundVariables).get)
	
	def cn: Option[Cn] = (numerator.cn, denominator.cn) match {
		case (Some(nu), Some(de)) => Some(nu / de)
		case _ => None
	}
	
	def simplify() = {
		if (cn.nonEmpty) cn.get
		else ApplyDivide(denominator.simplify, numerator.simplify)
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

object ApplyDivide {
	def apply(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(divide, numerator, denominator)
	def apply(numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(Divide(), numerator, denominator)
}