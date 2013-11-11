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

object G extends MathMLElem(MathML.h.prefix, "G", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def cnStep: Option[Constant] = None

	def simplifyStep() = G.this

	def variables: Set[String] = Set("x")

	def derivative(wrt: String) = if (wrt == "x") Gdx else Cn(0)

}
