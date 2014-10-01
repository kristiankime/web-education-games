package models.question.derivative

import com.artclod.mathml.{MathML, Math}
import com.artclod.mathml.scalar.apply._
import com.artclod.mathml.scalar.concept.{UnaryFunction, NthRoot, Logarithm, Constant}
import com.artclod.mathml.scalar.{Ci => Variable, Cn, MathMLElem}
import com.google.common.annotations.VisibleForTesting

import scala.util.Failure

object QuestionDifficulty {
  private val chainRulePoints = 13

  object MathType extends Enumeration {
    type MathType = Value
    val Con, Var, Fun = Value

    def apply(e: MathMLElem) = (e, e.variables.isEmpty) match {
      case (_, true) => Con
      case (v : Variable, _) => Var
      case (_, _) => Fun
    }
  }

  import MathType._

  def apply(e: MathMLElem): Double = e match {
      case m: Diff => m.diff // This object is designed for testing

      case m: Math => apply(m.value)

      case m: Constant => 1

      case m: Variable => 1

      case m : UnaryFunction => MathType(m.v) match {
        case Con => 1
        case Var => 8
        case Fun => 8 + chainRulePoints + apply(m.v)
      }

      case ApplyPlus(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + apply(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + apply(second)

        case (Fun, Con) => apply(first) + 1
        case (Fun, Var) => apply(first) + 1
        case (Fun, Fun) => apply(first) + 3 + apply(second)
      }

      case ApplyMinusB(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + apply(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + apply(second)

        case (Fun, Con) => apply(first) + 1
        case (Fun, Var) => apply(first) + 1
        case (Fun, Fun) => apply(first) + 3 + apply(second)
      }

      case ApplyMinusU(value) => MathType(value) match {
        case Con => 1
        case Var => 2
        case Fun => 1 + apply(value)
      }

      case ApplyPower(base, exp) => (MathType(base), MathType(exp)) match {
        case (Con, Con) => 2
        case (Con, Var) => 10
        case (Con, Fun) => 10 + apply(exp)

        case (Var, Con) => 2
        case (Var, Var) => 21
        case (Var, Fun) => 21 + apply(exp)

        case (Fun, Con) => 2 + chainRulePoints + apply(base)
        case (Fun, Var) => 21 + chainRulePoints + apply(base)
        case (Fun, Fun) => 21 + chainRulePoints + apply(base) + apply(exp)
      }

      case ApplyTimes(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + apply(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 5 + apply(second)

        case (Fun, Con) => apply(first) + 1
        case (Fun, Var) => apply(first) + 5
        case (Fun, Fun) => apply(first) + 5 + apply(second)
      }

      case ApplyDivide(numerator, denominator) => (MathType(numerator), MathType(denominator)) match {
        case (Con, Con) => 2
        case (Con, Var) => 3
        case (Con, Fun) => 8 + apply(denominator)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + 8 + apply(denominator)

        case (Fun, Con) => apply(numerator) + 1
        case (Fun, Var) => apply(numerator) + 8 + 1
        case (Fun, Fun) => apply(numerator) + 8 + apply(denominator)
      }

      case m: ApplyLn => MathType(m.v) match {
        case Con => 2
        case Var => 8
        case Fun => 8 + chainRulePoints + apply(m.v)
      }

      case m: ApplyLog10 => MathType(m.v) match {
        case Con => 2
        case Var => 10
        case Fun => 10 + chainRulePoints + apply(m.v)
      }

      case m: ApplyLog => MathType(m.v) match {
        case Con => 2
        case Var => 10
        case Fun => 10 + chainRulePoints + apply(m.v)
      }

      case m: ApplySqrt => MathType(m.v) match {
        case Con => 2
        case Var => 4
        case Fun => 4 + chainRulePoints + apply(m.v)
      }

      case m: NthRoot => MathType(m.v) match {
        case Con => 3
        case Var => 5
        case Fun => 5 + chainRulePoints + apply(m.v)
      }

      case _ => throw new IllegalStateException("Could not find difficulty, type not recognized [" + e + "]")
    }

  /**
   * Should only be used for testing when a desired difficulty level is needed
   *
   * @param diff difficulty value
   */
  case class Diff(diff: Double) extends MathMLElem(MathML.h.prefix, "Diff", MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

    def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException())

    def constant: Option[Constant] = None

    def simplifyStep() = this

    def variables: Set[String] = Set()

    def derivative(wrt: String) = Cn(0)

  }
}
