package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.Root
import mathml.scalar.concept._

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

}