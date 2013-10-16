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
			case "ci" => Success(Ci(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0)))
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
	
	private def applyable(xml: Elem): Try[Applyable] = {
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

	val h = <hack/> // This is a hack so we can get default XML meta data for default MathML objects

}


