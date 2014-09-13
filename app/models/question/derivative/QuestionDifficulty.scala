package models.question.derivative

import com.artclod.mathml.Math
import com.artclod.mathml.scalar.apply._
import com.artclod.mathml.scalar.concept.{UnaryFunction, NthRoot, Logarithm, Constant}
import com.artclod.mathml.scalar.{Ci => Variable, MathMLElem}
import com.google.common.annotations.VisibleForTesting

object QuestionDifficulty {
  private val chainRulePoints = 13

  def apply(question: Question): Int = d(question.mathML)

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

  @VisibleForTesting
  def d(e: MathMLElem): Int = e match {
      case m: Math => d(m.value)

      case m: Constant => 1

      case m: Variable => 1

      case m : UnaryFunction => MathType(m.v) match {
        case Con => 1
        case Var => 8
        case Fun => 8 + chainRulePoints + d(m.v)
      }

      case ApplyPlus(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + d(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + d(second)

        case (Fun, Con) => d(first) + 1
        case (Fun, Var) => d(first) + 1
        case (Fun, Fun) => d(first) + 3 + d(second)
      }

      case ApplyMinusB(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + d(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + d(second)

        case (Fun, Con) => d(first) + 1
        case (Fun, Var) => d(first) + 1
        case (Fun, Fun) => d(first) + 3 + d(second)
      }

      case ApplyMinusU(value) => MathType(value) match {
        case Con => 1
        case Var => 2
        case Fun => 1 + d(value)
      }

      case ApplyPower(base, exp) => (MathType(base), MathType(exp)) match {
        case (Con, Con) => 2
        case (Con, Var) => 10
        case (Con, Fun) => 10 + d(exp)

        case (Var, Con) => 2
        case (Var, Var) => 21
        case (Var, Fun) => 21 + d(exp)

        case (Fun, Con) => 2 + chainRulePoints + d(base)
        case (Fun, Var) => 21 + chainRulePoints + d(base)
        case (Fun, Fun) => 21 + chainRulePoints + d(base) + d(exp)
      }

      case ApplyTimes(first, second) => (MathType(first), MathType(second)) match {
        case (Con, Con) => 2
        case (Con, Var) => 2
        case (Con, Fun) => 1 + d(second)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 5 + d(second)

        case (Fun, Con) => d(first) + 1
        case (Fun, Var) => d(first) + 5
        case (Fun, Fun) => d(first) + 5 + d(second)
      }

      case ApplyDivide(numerator, denominator) => (MathType(numerator), MathType(denominator)) match {
        case (Con, Con) => 2
        case (Con, Var) => 3
        case (Con, Fun) => 8 + d(denominator)

        case (Var, Con) => 2
        case (Var, Var) => 2
        case (Var, Fun) => 1 + 8 + d(denominator)

        case (Fun, Con) => d(numerator) + 1
        case (Fun, Var) => d(numerator) + 8 + 1
        case (Fun, Fun) => d(numerator) + 8 + d(denominator)
      }

      case m: ApplyLn => MathType(m.v) match {
        case Con => 2
        case Var => 8
        case Fun => 8 + chainRulePoints + d(m.v)
      }

      case m: ApplyLog10 => MathType(m.v) match {
        case Con => 2
        case Var => 10
        case Fun => 10 + chainRulePoints + d(m.v)
      }

      case m: ApplyLog => MathType(m.v) match {
        case Con => 2
        case Var => 10
        case Fun => 10 + chainRulePoints + d(m.v)
      }

      case m: ApplySqrt => MathType(m.v) match {
        case Con => 2
        case Var => 4
        case Fun => 4 + chainRulePoints + d(m.v)
      }

      case m: NthRoot => MathType(m.v) match {
        case Con => 3
        case Var => 5
        case Fun => 5 + chainRulePoints + d(m.v)
      }

      case _ => throw new IllegalStateException("Could not find difficulty, type not recognized [" + e + "]")
    }

}
