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

object NeedsSimp extends MathMLElem(MathML.h.prefix, "NeedsSimp", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def constant: Option[Constant] = None

	def simplifyStep() = Simplified

	def variables: Set[String] = throw new UnsupportedOperationException()

	def derivative(wrt: String) = throw new UnsupportedOperationException()

}


object Simplified extends MathMLElem(MathML.h.prefix, "Simplified", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def constant: Option[Constant] = None

	def simplifyStep() = Simplified

	def variables: Set[String] = throw new UnsupportedOperationException()

	def derivative(wrt: String) = throw new UnsupportedOperationException()

}
