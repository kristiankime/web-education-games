package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._
import mathml.scalar._

case class ApplyCos(value: MathMLElem) extends Logarithm(ExponentialE.v, value, Seq(Sin): _*) {

	override def eval(boundVariables: Map[String, Double]) = Try(math.cos(v.eval(boundVariables).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.cos(v))
		case _ => None
	}

	def simplifyStep() = ApplyCos(v.s)

	def derivative(x: String) = -ApplySin(v.s) * v.d(x)

}