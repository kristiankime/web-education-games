package com.artclod.mathml.scalar

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import scala.util._
import com.artclod.mathml.scalar.concept.Constant

object Hdx extends MathMLElem(MathML.h.prefix, "Hdx", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def constant: Option[Constant] = None

	def simplifyStep() = Hdx.this

	def variables: Set[String] = Set("x")

	def derivative(wrt: String) = if (wrt == "x") throw new UnsupportedOperationException() else Cn(0)

	override def toMathJS: String = throw new UnsupportedOperationException

}
