package mathml

import scala.xml.NamespaceBinding
import scala.xml.MetaData
import scala.xml.Node
import scala.xml.Elem
import scala.xml.Text

object MathML {

	def apply(xml: Elem): MathMLElem = {
		xml.label match {
			case "apply" => Apply(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, applyable(xml.childElem(0)), xml.childElem.drop(1).map(MathML(_)): _*)
			case "cn" => Cn(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0))
			case _ => throw new IllegalArgumentException(xml.label + " was not recognized as a MathML element")
		}
	}

	def applyable(xml: Elem): Applyable = {
		xml.label match {
			case "plus" => Plus(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty)
			case _ => throw new IllegalArgumentException(xml.label + " was not recognized as an applyable MathML element")
		}
	}

	implicit class PimpedElem(e: Elem) {
		def childElem = e.child.collect(_ match { case x: Elem => x })
	}

	
	val h = <hack/>
}

sealed abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*)

case class Apply(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val applyFunction: MathMLElem,
	val applyValues: MathMLElem*)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](applyFunction) ++ applyValues): _*) {

	def this(applyFunction: MathMLElem, applyValues: MathMLElem*) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, applyFunction, applyValues: _*)
}

object Apply {
	def apply(applyFunction: MathMLElem, applyValues: MathMLElem*) = new Apply(applyFunction, applyValues: _*)
}

// Applyables (Things that can be the first element in a apply element, i.e. plus, minus, mult etc...) 
abstract class Applyable(
	override val prefix: String,
	override val label: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends MathMLElem(prefix, label, attributes1, scope, minimizeEmpty, Seq(): _*)

case class Plus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "plus", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
}

object Plus{
	def apply() = new Plus()
}


// Terminal Identifiers
case class Cn(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "cn", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)
}

object Cn {
	def apply(value: Node) = new Cn(value)
	def apply(value: String) = new Cn(Text(value))
}
