package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyLn(value: MathMLElem) extends Logarithm(ExponentialE.v, value, Seq(Ln): _*) {

	override def eval(boundVariables: Map[String, Double]) = Try(math.log(v.eval(boundVariables).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Logarithm.ln(v))
		case _ => None
	}

	def simplifyStep() = ApplyLn(v.s)

	def derivative(x: String) = {
		val f = v.s
		val fP = f.d(x)

		fP / f
	}

}