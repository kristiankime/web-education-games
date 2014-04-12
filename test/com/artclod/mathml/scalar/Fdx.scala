package mathml.scalar

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import scala.util._
import mathml.scalar.concept.Constant

object Fdx extends MathMLElem(MathML.h.prefix, "Fdx", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def constant: Option[Constant] = None

	def simplifyStep() = Fdx.this

	def variables: Set[String] = Set("x")

	def derivative(wrt: String) = if (wrt == "x") throw new UnsupportedOperationException() else Cn(0)

}
