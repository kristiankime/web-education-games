package models.quiz.question.support

import play.api.db.slick.Config.driver.simple._

object DerivativeOrder {
  val all = Vector(OneTwoThree, OneThreeTwo, TwoOneThree, TwoThreeOne, ThreeOneTwo, ThreeTwoOne)

  def apply(in: String) = all.find( _.asString == in) match {
    case Some(out) => out
    case None => throw new IllegalArgumentException("Could not find " + DerivativeOrder.getClass.getSimpleName + " for " + in)
  }

  implicit def string2DerivativeOrder = MappedColumnType.base[DerivativeOrder, String](
    derivativeOrder => derivativeOrder.asString,
    str => apply(str))
}

sealed trait DerivativeOrder {
  val asString : String
}

object OneTwoThree extends DerivativeOrder { val asString = "1-2-3" }
object OneThreeTwo extends DerivativeOrder { val asString = "1-3-2" }
object TwoOneThree extends DerivativeOrder { val asString = "2-1-3" }
object TwoThreeOne extends DerivativeOrder { val asString = "2-3-1" }
object ThreeOneTwo extends DerivativeOrder { val asString = "3-1-2" }
object ThreeTwoOne extends DerivativeOrder { val asString = "3-2-1" }

