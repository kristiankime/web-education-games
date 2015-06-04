package models.quiz.question.support

import play.api.data.FormError
import play.api.data.format.Formatter
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

  implicit def derivativeOrderFormatter: Formatter[DerivativeOrder] = new Formatter[DerivativeOrder] {
    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => Left(Seq(FormError(key, "key must map to a value", Nil)))
        case Some(txt) => DerivativeOrder.parse(txt)
      }
    }

    def unbind(key: String, value: DerivativeOrder) = Map(key -> value.asString)
  }

  private def parse(in: String) : Either[Seq[FormError], DerivativeOrder] = all.find( _.asString == in) match {
    case Some(out) => Right(out)
    case None => Left(Seq(FormError(in, "Could not find " + DerivativeOrder.getClass.getSimpleName + " for " + in)))
  }
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

