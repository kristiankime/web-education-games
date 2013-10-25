package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text
import scala.util.Failure

object Fx extends MathMLElem(MathML.h.prefix, "Fx", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def isZero = false

	def isOne = false

	def simplify() = this

	def variables: Set[String] = Set("X")

	def derivative(wrt: String) = if(wrt.toLowerCase() == "x") { Fxdx } else { Cn(0) }
}
