package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text
import scala.util.Failure

object Gxdx extends MathMLElem(MathML.h.prefix, "Gxdx", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def isZero = false

	def isOne = false

	def simplify() = this

	def variables: Set[String] = Set("X")

	def derivative(wrt: String) = if(wrt.toLowerCase() == "x") { throw new UnsupportedOperationException() } else { Cn(0) }
}
