package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text
import scala.util.Failure
import mathml.scalar.MathMLElem
import mathml.scalar.Cn

object G extends MathMLElem(MathML.h.prefix, "G", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

	def isZero = false

	def isOne = false

	def cn: Option[Cn] = None

	def simplify() = G.this

	def variables: Set[String] = Set("x")

	def derivative(wrt: String) = if (wrt == "x") Gdx else Cn(0)

}
