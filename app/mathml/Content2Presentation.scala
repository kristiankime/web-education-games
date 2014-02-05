package mathml

import mathml.scalar.MathMLElem
import xml.transform.{ RuleTransformer, RewriteRule }
import xml.{ NodeSeq, Node, Elem, Text }
import xml.Utility.trim

//object Content2Presentation {
//	
////	def apply(content: MathMLElem) = XSLTransform(content, Ctop.str).get
//	
//	val removeEmptyRule = new RuleTransformer(new RemoveEmptyTagsRule)
//
//	def apply(content: MathMLElem) = {
//		val pres = XSLTransform(content, Ctop.str).get
//		System.err.println("pres: " + pres)
//		val clean = removeEmptyRule.transform(pres)
//		System.err.println("clean: " + clean)
//		clean
//	}
//
//}

// http://devblog.point2.com/2011/03/21/removing-empty-xml-elements-in-scala/
class RemoveEmptyTagsRule extends RewriteRule {
	val whiteSpace = """[\s�⁢]*""" // NOTE THERE IS AN INVISIBLE UNICODE CHARACTER IN THIS STRING!!!! http://www.fileformat.info/info/unicode/char/2062/index.htm

	override def transform(n: Node) = n match {
		case e @ Elem(prefix, label, attributes, scope, child @ _*) if (emptyNode(e)) => NodeSeq.Empty
		case other => other
	}

	private def emptyNode(e: Node): Boolean = {
//		val eText = e.text
//		val eTextLen = e.text.length()
////		val eTextChar = e.text.charAt(0).toInt
//		val eTextClass = eText.getClass
//		val eTextIsEmpty = e.text.trim.isEmpty()
//		System.err.println(eText + " " + eText.getClass)
//		val emptyT = emptyText(e)
//		System.err.println(e + " e.text [" + e.text + "] emptyText(e) " + emptyText(e) + " emptyAttributes(e) " + emptyAttributes(e) + " emptyChild(e.child) " + emptyChild(e.child) )
		
		emptyText(e) && emptyAttributes(e) && emptyChild(e.child)
	}

	private def emptyText(e: scala.xml.Node): Boolean = e.text.isEmpty() || e.text.matches(whiteSpace)

	private def emptyAttributes(e: scala.xml.Node): Boolean = e.attributes.isEmpty || e.attributes.forall(_.value == null)

	private def emptyChild(children: Seq[Node]): Boolean = children.foldLeft(true)(_ && emptyNode(_))

}