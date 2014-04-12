package com.artclod.mathml.scalar.concept


import com.artclod.mathml.MathML
import com.artclod.mathml.scalar._
import scala.xml.MetaData
import scala.util.Try
import scala.xml.Node
import scala.math.BigDecimal.double2bigDecimal
import scala.xml._

abstract class UnaryFunction(val v: MathMLElem, pre: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq(pre) ++ v): _*) {

	def variables: Set[String] = v.variables
	
}