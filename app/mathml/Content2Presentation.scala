package mathml

import mathml.scalar.MathMLElem
import xml.transform.{ RuleTransformer, RewriteRule }
import xml.{ NodeSeq, Node, Elem, Text }
import xml.Utility.trim

object Content2Presentation {

	def apply(content: MathMLElem) = XSLTransform(content, Ctop.str).get

}

// http://devblog.point2.com/2011/03/21/removing-empty-xml-elements-in-scala/
class RemoveEmptyTagsRule extends RewriteRule {
	val whiteSpace = "[\\s�]*"

	override def transform(n: Node) = n match {
		case e @ Elem(prefix, label, attributes, scope, child @ _*) if (emptyNode(e)) => NodeSeq.Empty
		case other => other
	}

	private def emptyNode(e: Node): Boolean = { emptyText(e) && emptyAttributes(e) && emptyChild(e.child) }

	private def emptyText(e: scala.xml.Node): Boolean = e.text.matches(whiteSpace)

	private def emptyAttributes(e: scala.xml.Node): Boolean = e.attributes.isEmpty || e.attributes.forall(_.value == null)

	private def emptyChild(children: Seq[Node]): Boolean = children.foldLeft(true)(_ && emptyNode(_))

}