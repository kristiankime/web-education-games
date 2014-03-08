package mathml.scalar

import scala.util._
import scala.xml._
import scala.annotation.tailrec
import mathml._
import mathml.scalar.apply._
import mathml.scalar.concept._

abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*) {

	def eval(boundVariables: Map[String, Double]): Try[Double]

	def isZero: Boolean = c.map(_.isZero).getOrElse(false)

	def isOne: Boolean = c.map(_.isOne).getOrElse(false)

	/**
	 * Does one round of simplification on this element.
	 * Implementations of this method should not use the "s".
	 */
	protected def simplifyStep: MathMLElem

	private def simplifyStepWithCNCheck: MathMLElem = {
		val foo = c.getOrElse(simplifyStep)
		
		if( (this ?= foo) != mathml.Match.Yes ) {
			System.err.println(this)
			System.err.println("simplifyStepWithCNCheck simplified to")
			System.err.println(foo)
			System.err.println
		}
		
		foo
	}

	private var s_ : MathMLElem = null
	def s = //this
	{
		if (s_ == null) {
			s_ = simplifyRecurse(this)
		}
		
		if( (this ?= s_) != mathml.Match.Yes ) {
			System.err.println(this)
			System.err.println("s_ simplified to")
			System.err.println(s_)
			System.err.println
		}
		
		s_
//		this
	}

	@tailrec private def simplifyRecurse(elem: MathMLElem): MathMLElem = {
		val simp = elem.simplifyStepWithCNCheck
		
		if( (elem ?= simp) != mathml.Match.Yes ) {
			System.err.println(elem)
			System.err.println("simplifyRecurse simplified to")
			System.err.println(simp)
			System.err.println
		}
		
		if (simp == elem) {
			elem
		} else {
			simplifyRecurse(simp)
		}
	}

	protected def constant: Option[Constant]

	private var c_ : Option[Constant] = null
	def c = {
		if (c_ == null) {
			c_ = constant
		}
		
		for(CTest <- c_)
		if( (this ?= CTest) != mathml.Match.Yes ) {
			System.err.println(this)
			System.err.println("c_ simplified to")
			System.err.println(CTest)
			System.err.println
		}
		
		c_
	}

	def variables: Set[String]

	protected def derivative(wrt: String): MathMLElem

	def d(wrt: String) =
		if (!variables.contains(wrt)) `0`
		else derivative(wrt).s

	def dx = d("x")

	def +(m: MathMLElem) = ApplyPlus(this, m)

	def *(m: MathMLElem) = ApplyTimes(this, m)

	def -(m: MathMLElem) = ApplyMinusB(this, m)

	def unary_-() = ApplyMinusU(this)

	def /(m: MathMLElem) = ApplyDivide(this, m)

	def ^(m: MathMLElem) = ApplyPower(this, m)

	def ?=(e: MathMLElem) = MathMLEq.checkEq("x", this, e)

}
