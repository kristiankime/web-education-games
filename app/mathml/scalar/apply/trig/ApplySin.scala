package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplySin(value: MathMLElem) extends Logarithm(ExponentialE.v, value, Seq(Sin): _*) {

	override def eval(boundVariables: Map[String, Double]) = Try(math.sin(v.eval(boundVariables).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.sin(v))
		case _ => None
	}

	def simplifyStep() = ApplySin(v.s)

	def derivative(x: String) = ApplyCos(v.s) * v.d(x)

}