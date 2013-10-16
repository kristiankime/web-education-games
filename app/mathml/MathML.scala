package mathml

import scala.xml.NamespaceBinding
import scala.xml.MetaData
import scala.xml.Node
import scala.xml.Elem
import scala.xml.Text
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object MathML {

	def apply(xml: Elem): Try[MathMLElem] = {
		xml.label.toLowerCase match {
			case "apply" => applyElement(apply, xml)
			case "cn" => Success(Cn(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0)))
			case _ => Failure(new IllegalArgumentException(xml.label + " was not recognized as a MathML element"))
		}
	}

	private def applyElement(apply: scala.xml.Elem => scala.util.Try[mathml.MathMLElem], xml: scala.xml.Elem): scala.util.Try[mathml.MathMLElem] = {
		if (xml.childElem.isEmpty) {
			Failure(new IllegalArgumentException("Apply MathML Elements should have at least one child " + xml))
		} else {
			val applyElement = applyable(xml.childElem(0))
			val applyValues = xml.childElem.drop(1).map(MathML(_))
			val failure = Seq(applyElement).++(applyValues).find(_.isFailure)
			if (failure.nonEmpty) {
				failure.get
			} else {
				Success(Apply(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, applyElement.get, applyValues.map(_.get): _*))
			}
		}
	}
	
	def applyable(xml: Elem): Try[Applyable] = {
		xml.label.toLowerCase match {
			case "plus" => Success(Plus(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty))
			case "minus" => Success(Minus(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty))
			case "times" => Success(Times(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty))
			case "divide" => Success(Divide(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty))
			case "power" => Success(Power(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty))
			case _ => Failure(new IllegalArgumentException(xml.label + " was not recognized as an applyable MathML element"))
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

object Plus {
	def apply() = new Plus()
}

case class Minus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "minus", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
}

object Minus {
	def apply() = new Minus()
}

case class Times(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "times", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
}

object Times {
	def apply() = new Times()
}

case class Divide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "divide", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
}

object Divide {
	def apply() = new Divide()
}

case class Power(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "power", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
}

object Power {
	def apply() = new Power()
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
