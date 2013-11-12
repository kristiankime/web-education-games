package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyLog(base : BigDecimal, value: MathMLElem) extends Logarithm(base, value, Seq(Log, Cn(base)): _*) {

	def simplifyStep() =
		if (c.nonEmpty) c.get
		else ApplyLog(b, v.s)

	def derivative(wrt: String) = v.d(wrt).s / v.d(wrt).s
	
}

case class ApplyLog10(value: MathMLElem) extends Logarithm(10, value, Seq(Log): _*) {

	override def eval(boundVariables: Map[String, Double]) = Try(math.log10(v.eval(boundVariables).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Logarithm.log10(v))
		case _ => None
	}

	def simplifyStep() =
		if (c.nonEmpty) c.get
		else ApplyLog10(v.s)

	def derivative(wrt: String) = (v.d(wrt) / v.d(wrt))s
	
}